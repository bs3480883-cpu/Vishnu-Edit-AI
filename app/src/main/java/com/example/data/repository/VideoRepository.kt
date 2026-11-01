package com.example.data.repository

import android.content.Context
import com.example.R
import com.example.data.model.SavedPrompt
import com.example.data.model.StudioMediaCatalog
import com.example.data.model.VideoShowcase
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.snapshots
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.tasks.await
import java.security.MessageDigest
import java.util.UUID

class VideoRepository(private val db: FirebaseFirestore) {

    constructor(context: Context) : this(
        FirebaseFirestore.getInstance(
            context.applicationContext.getString(R.string.firestore_database_id)
        )
    )

    @Volatile
    var activeAdminSessionId: String? = null
        private set

    fun sha256Hex(input: String): String {
        val digest = MessageDigest.getInstance("SHA-256")
        val bytes = digest.digest(input.toByteArray(Charsets.UTF_8))
        return bytes.joinToString("") { "%02x".format(it) }
    }

    /**
     * Authenticates the Owner Admin using the private Admin password.
     * Hashes the password with SHA-256 and creates a server-verified session document at /admin_sessions/{sessionId}.
     * Firestore security rules reject the write with PERMISSION_DENIED if the password hash does not match.
     */
    suspend fun loginOwnerAdmin(
        password: String,
        customSessionId: String? = null
    ): Result<String> {
        val sessionId = customSessionId?.takeIf { it.isNotBlank() }
            ?: "admin_sess_${UUID.randomUUID().toString().replace("-", "_")}"
        val passkeyHash = sha256Hex(password.trim())
        val docRef = db.collection("admin_sessions").document(sessionId)

        val payload = mapOf(
            "sessionId" to sessionId,
            "passkeyHash" to passkeyHash,
            "createdAt" to FieldValue.serverTimestamp()
        )

        return try {
            docRef.set(payload).await()
            activeAdminSessionId = sessionId
            Result.success(sessionId)
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            handleFirestoreError(e, OperationType.CREATE, docRef.path)
            Result.failure(e)
        }
    }

    /**
     * Verifies whether a stored adminSessionId is still valid on the server.
     */
    suspend fun verifyAdminSession(sessionId: String): Boolean {
        if (sessionId.isBlank()) return false
        val path = "admin_sessions/$sessionId"
        return try {
            val snap = db.collection("admin_sessions").document(sessionId).get().await()
            val exists = snap.exists()
            if (exists) {
                activeAdminSessionId = sessionId
            }
            exists
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            handleFirestoreError(e, OperationType.GET, path)
            false
        }
    }

    /**
     * Revokes the current Owner Admin session on the server and clears local admin state.
     */
    suspend fun logoutOwnerAdmin(): Result<Unit> {
        val currentSession = activeAdminSessionId
        activeAdminSessionId = null
        if (currentSession.isNullOrBlank()) return Result.success(Unit)
        val docRef = db.collection("admin_sessions").document(currentSession)
        return try {
            docRef.delete().await()
            Result.success(Unit)
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            handleFirestoreError(e, OperationType.DELETE, docRef.path)
            Result.failure(e)
        }
    }

    /**
     * Real-time stream of published videos for public visitors (no login required).
     * Aligns with firestore.rules `allow list: if existing().published == true`.
     */
    fun observePublishedVideos(): Flow<List<VideoShowcase>> = flow {
        val path = "videos"
        val sourceFlow = db.collection("videos")
            .whereEqualTo("published", true)
            .snapshots()
            .map { snapshot ->
                snapshot.documents.mapNotNull { doc ->
                    doc.toObject(
                        VideoShowcase::class.java,
                        DocumentSnapshot.ServerTimestampBehavior.ESTIMATE
                    )
                }.filter { !it.pendingDelete }
                    .sortedByDescending { it.createdAt?.seconds ?: 0L }
            }
            .catch { error ->
                if (error is Exception) {
                    handleFirestoreError(error, OperationType.LIST, path)
                }
                throw error
            }
        emitAll(sourceFlow)
    }

    /**
     * Real-time stream of ALL videos uploaded by the Owner (both published and unpublished drafts)
     * for the private Owner Admin Dashboard.
     */
    fun observeAdminVideos(): Flow<List<VideoShowcase>> = flow {
        val path = "videos"
        val sourceFlow = db.collection("videos")
            .whereEqualTo("authorId", StudioMediaCatalog.STUDIO_AUTHOR_ID)
            .snapshots()
            .map { snapshot ->
                snapshot.documents.mapNotNull { doc ->
                    doc.toObject(
                        VideoShowcase::class.java,
                        DocumentSnapshot.ServerTimestampBehavior.ESTIMATE
                    )
                }.filter { !it.pendingDelete }
                    .sortedByDescending { it.createdAt?.seconds ?: 0L }
            }
            .catch { error ->
                if (error is Exception) {
                    handleFirestoreError(error, OperationType.LIST, path)
                }
                throw error
            }
        emitAll(sourceFlow)
    }

    /**
     * Fetches a single published video by ID (accessible to public visitors without login).
     */
    suspend fun getVideoById(videoId: String): Result<VideoShowcase> {
        val docRef = db.collection("videos").document(videoId)
        return try {
            val snap = docRef.get().await()
            val video = snap.toObject(
                VideoShowcase::class.java,
                DocumentSnapshot.ServerTimestampBehavior.ESTIMATE
            ) ?: throw NoSuchElementException("Video not found: $videoId")
            Result.success(video)
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            handleFirestoreError(e, OperationType.GET, docRef.path)
            Result.failure(e)
        }
    }

    /**
     * Creates a new AI Video Showcase entry (Owner Admin only — requires valid server-verified adminSessionId).
     */
    suspend fun createVideo(
        title: String,
        description: String,
        videoUrl: String,
        thumbnailUrl: String,
        prompt: String,
        category: String,
        published: Boolean,
        featured: Boolean,
        popularPrompt: Boolean,
        durationSeconds: Long = 15L,
        aspectRatio: String = "16:9",
        aiModel: String = "Veo 2 Cinema",
        customDocId: String? = null,
        sessionIdOverride: String? = null
    ): Result<String> {
        val sessionId = sessionIdOverride ?: activeAdminSessionId ?: "unauthenticated_visitor_session"
        val videosRef = db.collection("videos")
        val docRef = if (!customDocId.isNullOrBlank()) {
            videosRef.document(customDocId)
        } else {
            videosRef.document()
        }

        val payload = mapOf(
            "title" to title.trim(),
            "description" to description.trim(),
            "videoUrl" to videoUrl.trim(),
            "thumbnailUrl" to thumbnailUrl.trim(),
            "prompt" to prompt.trim(),
            "category" to category,
            "published" to published,
            "featured" to featured,
            "popularPrompt" to popularPrompt,
            "durationSeconds" to durationSeconds,
            "aspectRatio" to aspectRatio.trim().ifEmpty { "16:9" },
            "aiModel" to aiModel.trim().ifEmpty { "Veo 2 Cinema" },
            "authorId" to StudioMediaCatalog.STUDIO_AUTHOR_ID,
            "adminSessionId" to sessionId,
            "pendingDelete" to false,
            "createdAt" to FieldValue.serverTimestamp(),
            "updatedAt" to FieldValue.serverTimestamp()
        )

        return try {
            docRef.set(payload).await()
            Result.success(docRef.id)
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            handleFirestoreError(e, OperationType.CREATE, docRef.path)
            Result.failure(e)
        }
    }

    /**
     * Updates an existing AI Video Showcase entry (Owner Admin only — verified by server via adminSessionId).
     */
    suspend fun updateVideo(
        video: VideoShowcase,
        sessionIdOverride: String? = null
    ): Result<Unit> {
        require(video.id.isNotBlank()) { "Video ID must not be blank for update" }
        val existingCreatedAt = requireNotNull(video.createdAt) { "Video createdAt must not be null for update" }
        val sessionId = sessionIdOverride ?: activeAdminSessionId ?: "unauthenticated_visitor_session"

        val docRef = db.collection("videos").document(video.id)
        val payload = mapOf(
            "title" to video.title.trim(),
            "description" to video.description.trim(),
            "videoUrl" to video.videoUrl.trim(),
            "thumbnailUrl" to video.thumbnailUrl.trim(),
            "prompt" to video.prompt.trim(),
            "category" to video.category,
            "published" to video.published,
            "featured" to video.featured,
            "popularPrompt" to video.popularPrompt,
            "durationSeconds" to video.durationSeconds,
            "aspectRatio" to video.aspectRatio.trim().ifEmpty { "16:9" },
            "aiModel" to video.aiModel.trim().ifEmpty { "Veo 2 Cinema" },
            "authorId" to video.authorId.ifBlank { StudioMediaCatalog.STUDIO_AUTHOR_ID },
            "adminSessionId" to sessionId,
            "pendingDelete" to video.pendingDelete,
            "createdAt" to existingCreatedAt,
            "updatedAt" to FieldValue.serverTimestamp()
        )
        return try {
            docRef.set(payload).await()
            Result.success(Unit)
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            handleFirestoreError(e, OperationType.UPDATE, docRef.path)
            Result.failure(e)
        }
    }

    /**
     * Toggles the publish status of a video (Owner Admin only).
     */
    suspend fun setVideoPublishStatus(video: VideoShowcase, published: Boolean): Result<Unit> {
        return updateVideo(video.copy(published = published))
    }

    /**
     * Deletes a video from Firestore (Owner Admin only).
     * Executes two-step server-authorized deletion:
     * 1. Marks the video with pendingDelete = true using a valid server-verified adminSessionId.
     * 2. Deletes the document once firestore.rules confirms pendingDelete == true && isAuthorizedAdminSession.
     */
    suspend fun deleteVideo(
        video: VideoShowcase,
        sessionIdOverride: String? = null
    ): Result<Unit> {
        require(video.id.isNotBlank()) { "Video ID must not be blank for delete" }
        val sessionId = sessionIdOverride ?: activeAdminSessionId ?: "unauthenticated_visitor_session"
        val docRef = db.collection("videos").document(video.id)

        // Step 1: Authorize deletion via server-verified update
        val markResult = updateVideo(
            video = video.copy(pendingDelete = true),
            sessionIdOverride = sessionId
        )
        if (markResult.isFailure) {
            return markResult
        }

        // Step 2: Execute document deletion
        return try {
            docRef.delete().await()
            Result.success(Unit)
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            handleFirestoreError(e, OperationType.DELETE, docRef.path)
            Result.failure(e)
        }
    }

    /**
     * Ensures the 6 initial production AI showcase videos are seeded once on first database launch
     * so public visitors opening the app immediately see published videos across all 6 categories.
     */
    suspend fun ensureInitialShowcaseSeeded(): Result<Boolean> {
        val bootstrapRef = db.collection("studio_config").document("bootstrap")
        return try {
            val snap = bootstrapRef.get().await()
            if (snap.exists() && snap.getBoolean("seeded") == true) {
                return Result.success(false)
            }
            val prevSession = activeAdminSessionId
            val seedSessionId = prevSession ?: run {
                val res = loginOwnerAdmin(
                    password = StudioMediaCatalog.DEFAULT_ADMIN_PASSWORD,
                    customSessionId = "studio_bootstrap_session"
                )
                activeAdminSessionId = prevSession
                res.getOrThrow()
            }
            val seedRes = seedInitialShowcaseVideos(sessionIdOverride = seedSessionId)
            if (seedRes.isSuccess) {
                bootstrapRef.set(
                    mapOf(
                        "seeded" to true,
                        "updatedAt" to FieldValue.serverTimestamp()
                    )
                ).await()
                Result.success(true)
            } else {
                Result.failure(seedRes.exceptionOrNull() ?: Exception("Failed to seed initial catalog"))
            }
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * Seeds or restores the 6 production AI showcase videos.
     */
    suspend fun seedInitialShowcaseVideos(sessionIdOverride: String? = null): Result<Int> {
        val sessionId = sessionIdOverride ?: activeAdminSessionId ?: "unauthenticated_visitor_session"
        var count = 0
        for ((index, preset) in StudioMediaCatalog.presets.withIndex()) {
            val docId = "vishnu_showcase_${index + 1}"
            val res = createVideo(
                title = preset.defaultTitle,
                description = preset.defaultDescription,
                videoUrl = preset.videoUrl,
                thumbnailUrl = preset.thumbnailUrl,
                prompt = preset.defaultPrompt,
                category = preset.category.displayName,
                published = true,
                featured = preset.featured,
                popularPrompt = preset.popularPrompt,
                durationSeconds = preset.durationSeconds,
                aspectRatio = preset.aspectRatio,
                aiModel = preset.aiModel,
                customDocId = docId,
                sessionIdOverride = sessionId
            )
            if (res.isSuccess) {
                count++
            } else {
                return Result.failure(res.exceptionOrNull() ?: Exception("Failed to seed showcase"))
            }
        }
        return Result.success(count)
    }

    /**
     * Observes the visitor's saved prompts collection (no login required).
     */
    fun observeSavedPrompts(visitorId: String): Flow<List<SavedPrompt>> = flow {
        val path = "visitors/$visitorId/saved_prompts"
        val sourceFlow = db.collection("visitors")
            .document(visitorId)
            .collection("saved_prompts")
            .whereEqualTo("visitorId", visitorId)
            .snapshots()
            .map { snapshot ->
                snapshot.documents.mapNotNull { doc ->
                    doc.toObject(
                        SavedPrompt::class.java,
                        DocumentSnapshot.ServerTimestampBehavior.ESTIMATE
                    )
                }.sortedByDescending { it.createdAt?.seconds ?: 0L }
            }
            .catch { error ->
                if (error is Exception) {
                    handleFirestoreError(error, OperationType.LIST, path)
                }
                throw error
            }
        emitAll(sourceFlow)
    }

    /**
     * Saves/bookmarks an AI prompt from a video to the visitor's device collection (no login required).
     */
    suspend fun savePromptForVisitor(
        visitorId: String,
        video: VideoShowcase,
        notes: String = ""
    ): Result<String> {
        val safeDocId = "saved_${video.id.ifBlank { "custom" }}"
        val docRef = db.collection("visitors")
            .document(visitorId)
            .collection("saved_prompts")
            .document(safeDocId)

        val payload = mapOf(
            "visitorId" to visitorId,
            "videoId" to video.id.ifBlank { "custom_id" },
            "videoTitle" to video.title.take(150),
            "prompt" to video.prompt.take(4000),
            "category" to video.category,
            "thumbnailUrl" to video.thumbnailUrl.take(1000),
            "aiModel" to video.aiModel.take(80),
            "notes" to notes.take(500),
            "createdAt" to FieldValue.serverTimestamp(),
            "updatedAt" to FieldValue.serverTimestamp()
        )

        return try {
            docRef.set(payload).await()
            Result.success(docRef.id)
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            handleFirestoreError(e, OperationType.CREATE, docRef.path)
            Result.failure(e)
        }
    }

    /**
     * Updates notes on a saved prompt.
     */
    suspend fun updateSavedPromptNotes(
        visitorId: String,
        savedPrompt: SavedPrompt,
        newNotes: String
    ): Result<Unit> {
        require(savedPrompt.id.isNotBlank()) { "SavedPrompt ID must not be blank" }
        val existingCreatedAt = requireNotNull(savedPrompt.createdAt) { "SavedPrompt createdAt must not be null" }

        val docRef = db.collection("visitors")
            .document(visitorId)
            .collection("saved_prompts")
            .document(savedPrompt.id)

        val payload = mapOf(
            "visitorId" to visitorId,
            "videoId" to savedPrompt.videoId,
            "videoTitle" to savedPrompt.videoTitle,
            "prompt" to savedPrompt.prompt,
            "category" to savedPrompt.category,
            "thumbnailUrl" to savedPrompt.thumbnailUrl,
            "aiModel" to savedPrompt.aiModel,
            "notes" to newNotes.take(500),
            "createdAt" to existingCreatedAt,
            "updatedAt" to FieldValue.serverTimestamp()
        )

        return try {
            docRef.set(payload).await()
            Result.success(Unit)
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            handleFirestoreError(e, OperationType.UPDATE, docRef.path)
            Result.failure(e)
        }
    }

    /**
     * Removes a saved prompt from the visitor's collection.
     */
    suspend fun removeSavedPrompt(visitorId: String, savedId: String): Result<Unit> {
        val docRef = db.collection("visitors")
            .document(visitorId)
            .collection("saved_prompts")
            .document(savedId)
        return try {
            docRef.delete().await()
            Result.success(Unit)
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            handleFirestoreError(e, OperationType.DELETE, docRef.path)
            Result.failure(e)
        }
    }
}
