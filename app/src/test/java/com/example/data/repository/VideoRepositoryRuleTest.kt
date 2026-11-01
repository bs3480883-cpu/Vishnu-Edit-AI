package com.example.data.repository

import com.example.base.FirestoreEmulatorTestBase
import com.example.data.model.StudioMediaCatalog
import com.example.data.model.VideoCategory
import com.example.data.model.VideoShowcase
import com.google.firebase.firestore.FirebaseFirestoreException
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withTimeout
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test
import java.util.UUID

class VideoRepositoryRuleTest : FirestoreEmulatorTestBase() {

    @Test
    fun ownerAdmin_passwordLogin_canCreateUpdateAndDeleteVideo() = runBlocking {
        auth.signOut()
        val repository = VideoRepository(firestore)

        // Wrong password must be rejected by Firestore server rules
        val badLogin = withTimeout(DEFAULT_TIMEOUT_MS) {
            repository.loginOwnerAdmin("WrongPassword123")
        }
        assertTrue("Wrong admin password must fail", badLogin.isFailure)
        assertEquals(
            FirebaseFirestoreException.Code.PERMISSION_DENIED,
            badLogin.exceptionOrNull()?.unwrapFirestoreException()?.code
        )

        // Valid Owner password succeeds on server
        val validLogin = withTimeout(DEFAULT_TIMEOUT_MS) {
            repository.loginOwnerAdmin(StudioMediaCatalog.DEFAULT_ADMIN_PASSWORD)
        }
        assertTrue("Owner admin login should succeed", validLogin.isSuccess)
        val sessionId = validLogin.getOrThrow()
        assertTrue(
            "verifyAdminSession should return true",
            withTimeout(DEFAULT_TIMEOUT_MS) { repository.verifyAdminSession(sessionId) }
        )

        val docId = "test_video_${UUID.randomUUID().toString().replace("-", "_")}"
        val createRes = withTimeout(DEFAULT_TIMEOUT_MS) {
            repository.createVideo(
                title = "Cyberpunk Horizon AI",
                description = "Cinematic 8K AI sequence in rain.",
                videoUrl = "https://commondatastorage.googleapis.com/gtv-videos-bucket/sample/ForBiggerBlazes.mp4",
                thumbnailUrl = "https://images.unsplash.com/photo-1518709268805-4e9042af9f23?w=800",
                prompt = "Cinematic wide shot of futuristic cyberpunk city in heavy rain, 8k",
                category = VideoCategory.CINEMATIC.displayName,
                published = true,
                featured = true,
                popularPrompt = true,
                durationSeconds = 15L,
                aspectRatio = "16:9",
                aiModel = "Veo 2 Cinema",
                customDocId = docId
            )
        }
        assertTrue("Owner should create video", createRes.isSuccess)

        val fetched = withTimeout(DEFAULT_TIMEOUT_MS) { repository.getVideoById(docId).getOrThrow() }
        assertEquals("Cyberpunk Horizon AI", fetched.title)

        val updateRes = withTimeout(DEFAULT_TIMEOUT_MS) {
            repository.updateVideo(fetched.copy(title = "Updated Horizon AI", published = false))
        }
        assertTrue("Owner should update and unpublish video", updateRes.isSuccess)

        val deleteRes = withTimeout(DEFAULT_TIMEOUT_MS) {
            repository.deleteVideo(fetched.copy(title = "Updated Horizon AI", published = false))
        }
        assertTrue("Owner should delete video", deleteRes.isSuccess)
    }

    @Test
    fun publicVisitor_noLogin_canReadPublished_cannotReadDrafts_andCannotWrite() = runBlocking {
        auth.signOut()
        // 1. Owner logs in with password and creates a published video and an unpublished draft
        val ownerRepo = VideoRepository(firestore)
        withTimeout(DEFAULT_TIMEOUT_MS) {
            ownerRepo.loginOwnerAdmin(StudioMediaCatalog.DEFAULT_ADMIN_PASSWORD).getOrThrow()
        }

        val pubId = "pub_${UUID.randomUUID().toString().replace("-", "_")}"
        val draftId = "draft_${UUID.randomUUID().toString().replace("-", "_")}"

        withTimeout(DEFAULT_TIMEOUT_MS) {
            ownerRepo.createVideo(
                title = "Public AI Video",
                description = "Visible to everyone without login",
                videoUrl = "https://commondatastorage.googleapis.com/gtv-videos-bucket/sample/ForBiggerBlazes.mp4",
                thumbnailUrl = "https://images.unsplash.com/photo-1518709268805-4e9042af9f23?w=800",
                prompt = "Public prompt text",
                category = VideoCategory.CINEMATIC.displayName,
                published = true,
                featured = false,
                popularPrompt = false,
                customDocId = pubId
            ).getOrThrow()

            ownerRepo.createVideo(
                title = "Private Draft Video",
                description = "Only visible in Owner Admin Dashboard",
                videoUrl = "https://commondatastorage.googleapis.com/gtv-videos-bucket/sample/ForBiggerBlazes.mp4",
                thumbnailUrl = "https://images.unsplash.com/photo-1518709268805-4e9042af9f23?w=800",
                prompt = "Secret prompt text",
                category = VideoCategory.VFX.displayName,
                published = false,
                featured = false,
                popularPrompt = false,
                customDocId = draftId
            ).getOrThrow()
        }

        // 2. Public visitor with NO login at all
        val visitorRepo = VideoRepository(firestore)

        // Visitor CAN read published video without login
        val pubVideo = withTimeout(DEFAULT_TIMEOUT_MS) { visitorRepo.getVideoById(pubId) }
        assertTrue("Visitor can read published video without login", pubVideo.isSuccess)

        // Visitor CAN observe published videos stream without login
        val pubList = withTimeout(FLOW_TIMEOUT_MS) {
            visitorRepo.observePublishedVideos().first { list -> list.any { it.id == pubId } }
        }
        assertTrue("Visitor stream includes published video", pubList.any { it.id == pubId })

        // Visitor CANNOT read unpublished draft video
        val draftVideo = withTimeout(DEFAULT_TIMEOUT_MS) { visitorRepo.getVideoById(draftId) }
        assertTrue("Visitor cannot read unpublished video", draftVideo.isFailure)
        val draftEx = draftVideo.exceptionOrNull()?.unwrapFirestoreException()
        assertNotNull(draftEx)
        assertEquals(FirebaseFirestoreException.Code.PERMISSION_DENIED, draftEx?.code)

        // Visitor CANNOT create video
        val visitorCreate = withTimeout(DEFAULT_TIMEOUT_MS) {
            visitorRepo.createVideo(
                title = "Unauthorized Upload",
                description = "Should fail",
                videoUrl = "https://example.com/v.mp4",
                thumbnailUrl = "https://example.com/t.jpg",
                prompt = "Unauthorized prompt",
                category = VideoCategory.CINEMATIC.displayName,
                published = true,
                featured = false,
                popularPrompt = false
            )
        }
        assertTrue("Visitor create must fail", visitorCreate.isFailure)
        assertEquals(
            FirebaseFirestoreException.Code.PERMISSION_DENIED,
            visitorCreate.exceptionOrNull()?.unwrapFirestoreException()?.code
        )

        // Visitor CANNOT delete published video
        val visitorDelete = withTimeout(DEFAULT_TIMEOUT_MS) {
            visitorRepo.deleteVideo(pubVideo.getOrThrow())
        }
        assertTrue("Visitor delete must fail", visitorDelete.isFailure)
        assertEquals(
            FirebaseFirestoreException.Code.PERMISSION_DENIED,
            visitorDelete.exceptionOrNull()?.unwrapFirestoreException()?.code
        )
    }

    @Test
    fun publicVisitor_canSaveAndManagePromptsWithoutLogin() = runBlocking {
        auth.signOut()
        val visitorId = "visitor_${UUID.randomUUID().toString().replace("-", "_")}"
        val visitorRepo = VideoRepository(firestore)

        val uniqueVideoId = "vid_sample_${UUID.randomUUID().toString().replace("-", "_")}"
        val sampleVideo = VideoShowcase(
            id = uniqueVideoId,
            title = "Sample AI Film",
            prompt = "Anamorphic sci-fi desert landscape, 8k",
            category = VideoCategory.SHORT_FILMS.displayName,
            thumbnailUrl = "https://images.unsplash.com/photo-1451187580459-43490279c0fa?w=800",
            aiModel = "Veo 2 Cinema"
        )
        val saveRes = withTimeout(DEFAULT_TIMEOUT_MS) {
            visitorRepo.savePromptForVisitor(visitorId, sampleVideo, "Great lighting setup")
        }
        assertTrue("Visitor should save prompt without login", saveRes.isSuccess)

        val savedPrompts = withTimeout(FLOW_TIMEOUT_MS) {
            visitorRepo.observeSavedPrompts(visitorId).first { list -> list.any { it.videoId == uniqueVideoId } }
        }
        val savedItem = savedPrompts.first { it.videoId == uniqueVideoId }

        val updateRes = withTimeout(DEFAULT_TIMEOUT_MS) {
            visitorRepo.updateSavedPromptNotes(visitorId, savedItem, "Updated production notes")
        }
        assertTrue("Visitor should update saved prompt notes", updateRes.isSuccess)

        val removeRes = withTimeout(DEFAULT_TIMEOUT_MS) {
            visitorRepo.removeSavedPrompt(visitorId, savedItem.id)
        }
        assertTrue("Visitor should remove saved prompt", removeRes.isSuccess)
    }

    private companion object {
        const val DEFAULT_TIMEOUT_MS = 5000L
        const val FLOW_TIMEOUT_MS = 4000L
    }
}
