package com.example.ui.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.model.SavedPrompt
import com.example.data.model.VideoShowcase
import com.example.data.repository.VideoRepository
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

sealed interface UiState<out T> {
    data object Loading : UiState<Nothing>
    data class Success<T>(val data: T) : UiState<T>
    data class Error(val message: String) : UiState<Nothing>
}

enum class MainTab {
    HOME,
    CATEGORIES,
    SEARCH,
    SAVED_PROMPTS,
    PROFILE
}

class VishnuEditViewModel(
    private val repository: VideoRepository,
    private val visitorId: String
) : ViewModel() {

    private val _isAdmin = MutableStateFlow(false)
    val isAdmin: StateFlow<Boolean> = _isAdmin.asStateFlow()

    private val _selectedTab = MutableStateFlow(MainTab.HOME)
    val selectedTab: StateFlow<MainTab> = _selectedTab.asStateFlow()

    private val _selectedCategory = MutableStateFlow<String?>(null)
    val selectedCategory: StateFlow<String?> = _selectedCategory.asStateFlow()

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    private val _selectedVideoForDetail = MutableStateFlow<VideoShowcase?>(null)
    val selectedVideoForDetail: StateFlow<VideoShowcase?> = _selectedVideoForDetail.asStateFlow()

    private val _isAdminPortalOpen = MutableStateFlow(false)
    val isAdminPortalOpen: StateFlow<Boolean> = _isAdminPortalOpen.asStateFlow()

    private val _adminLoginError = MutableStateFlow<String?>(null)
    val adminLoginError: StateFlow<String?> = _adminLoginError.asStateFlow()

    private val _editingVideo = MutableStateFlow<VideoShowcase?>(null)
    val editingVideo: StateFlow<VideoShowcase?> = _editingVideo.asStateFlow()

    private val _isUploadSheetOpen = MutableStateFlow(false)
    val isUploadSheetOpen: StateFlow<Boolean> = _isUploadSheetOpen.asStateFlow()

    private val _statusBanner = MutableStateFlow<String?>(null)
    val statusBanner: StateFlow<String?> = _statusBanner.asStateFlow()

    private val _isSubmitting = MutableStateFlow(false)
    val isSubmitting: StateFlow<Boolean> = _isSubmitting.asStateFlow()

    val publishedVideosState: StateFlow<UiState<List<VideoShowcase>>> =
        repository.observePublishedVideos()
            .map<List<VideoShowcase>, UiState<List<VideoShowcase>>> { list ->
                UiState.Success(list)
            }
            .catch { error ->
                Log.w(TAG, "Error observing published videos", error)
                emit(UiState.Error(error.localizedMessage ?: "Failed to load published videos"))
            }
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(STOP_TIMEOUT_MILLIS),
                initialValue = UiState.Loading
            )

    @OptIn(ExperimentalCoroutinesApi::class)
    val adminVideosState: StateFlow<UiState<List<VideoShowcase>>> =
        _isAdmin.flatMapLatest { admin ->
            if (!admin) {
                flowOf(UiState.Success(emptyList()))
            } else {
                repository.observeAdminVideos()
                    .map<List<VideoShowcase>, UiState<List<VideoShowcase>>> { UiState.Success(it) }
                    .catch { error ->
                        Log.w(TAG, "Error observing admin videos", error)
                        emit(UiState.Error(error.localizedMessage ?: "Failed to load admin videos"))
                    }
            }
        }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(STOP_TIMEOUT_MILLIS),
            initialValue = UiState.Loading
        )

    val savedPromptsState: StateFlow<UiState<List<SavedPrompt>>> =
        repository.observeSavedPrompts(visitorId)
            .map<List<SavedPrompt>, UiState<List<SavedPrompt>>> { UiState.Success(it) }
            .catch { error ->
                Log.w(TAG, "Error observing saved prompts", error)
                emit(UiState.Error(error.localizedMessage ?: "Failed to load saved prompts"))
            }
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(STOP_TIMEOUT_MILLIS),
                initialValue = UiState.Loading
            )

    init {
        viewModelScope.launch {
            repository.ensureInitialShowcaseSeeded()
        }
    }

    fun selectTab(tab: MainTab) {
        _selectedTab.value = tab
    }

    fun selectCategoryAndOpen(categoryName: String?) {
        _selectedCategory.value = categoryName
        _selectedTab.value = MainTab.CATEGORIES
    }

    fun updateSearchQuery(query: String) {
        _searchQuery.value = query
    }

    fun openSearchWithQuery(query: String) {
        _searchQuery.value = query
        _selectedTab.value = MainTab.SEARCH
    }

    fun openVideoDetail(video: VideoShowcase) {
        _selectedVideoForDetail.value = video
    }

    fun closeVideoDetail() {
        _selectedVideoForDetail.value = null
    }

    /**
     * Opens the private Owner Admin Portal screen (renders Password Login if not yet authenticated,
     * or the Owner Admin Dashboard if already authenticated).
     */
    fun openAdminPortal() {
        _adminLoginError.value = null
        _isAdminPortalOpen.value = true
    }

    fun closeAdminPortal() {
        _isAdminPortalOpen.value = false
        _isUploadSheetOpen.value = false
        _editingVideo.value = null
        _adminLoginError.value = null
    }

    fun clearAdminLoginError() {
        _adminLoginError.value = null
    }

    /**
     * Authenticates the Owner using the private Admin Password against Firestore security rules.
     */
    fun loginAdminWithPassword(password: String) {
        if (password.isBlank()) {
            _adminLoginError.value = "Please enter the Owner Admin password."
            return
        }
        viewModelScope.launch {
            _isSubmitting.value = true
            _adminLoginError.value = null
            val res = repository.loginOwnerAdmin(password.trim())
            _isSubmitting.value = false
            if (res.isSuccess) {
                _isAdmin.value = true
                _isAdminPortalOpen.value = true
                showBanner("Owner Admin Verified & Unlocked")
            } else {
                _adminLoginError.value = "Invalid Owner Password. Server rejected authorization."
            }
        }
    }

    /**
     * Logs out of the private Owner Admin Dashboard and revokes the server session.
     */
    fun logoutAdmin() {
        viewModelScope.launch {
            repository.logoutOwnerAdmin()
            _isAdmin.value = false
            _isAdminPortalOpen.value = false
            _isUploadSheetOpen.value = false
            _editingVideo.value = null
            showBanner("Owner Admin Session Locked")
        }
    }

    fun startCreateNewVideo() {
        if (!_isAdmin.value) {
            showBanner("Unauthorized: Only the Owner can upload content.")
            return
        }
        _editingVideo.value = null
        _isUploadSheetOpen.value = true
    }

    fun startEditingVideo(video: VideoShowcase) {
        if (!_isAdmin.value) {
            showBanner("Unauthorized: Only the Owner can edit content.")
            return
        }
        _editingVideo.value = video
        _isAdminPortalOpen.value = true
        _isUploadSheetOpen.value = true
    }

    fun dismissUploadEditForm() {
        _isUploadSheetOpen.value = false
        _editingVideo.value = null
    }

    fun showBanner(message: String) {
        _statusBanner.value = message
    }

    fun clearBanner() {
        _statusBanner.value = null
    }

    fun notifyPromptCopied() {
        _statusBanner.value = "Prompt Copied Successfully"
    }

    fun seedShowcaseCatalog() {
        if (!_isAdmin.value) return
        viewModelScope.launch {
            _isSubmitting.value = true
            val res = repository.seedInitialShowcaseVideos()
            _isSubmitting.value = false
            if (res.isSuccess) {
                showBanner("Published ${res.getOrDefault(6)} Studio AI Showcase Videos")
            } else {
                showBanner(res.exceptionOrNull()?.localizedMessage ?: "Failed to load studio showcase")
            }
        }
    }

    fun saveVideoForm(
        title: String,
        description: String,
        videoUrl: String,
        thumbnailUrl: String,
        prompt: String,
        category: String,
        published: Boolean,
        featured: Boolean,
        popularPrompt: Boolean,
        durationSeconds: Long,
        aspectRatio: String,
        aiModel: String
    ) {
        if (!_isAdmin.value) {
            showBanner("Server Authorization Error: Only the Owner can save content.")
            return
        }
        val existing = _editingVideo.value
        viewModelScope.launch {
            _isSubmitting.value = true
            if (existing == null) {
                val res = repository.createVideo(
                    title = title,
                    description = description,
                    videoUrl = videoUrl,
                    thumbnailUrl = thumbnailUrl,
                    prompt = prompt,
                    category = category,
                    published = published,
                    featured = featured,
                    popularPrompt = popularPrompt,
                    durationSeconds = durationSeconds,
                    aspectRatio = aspectRatio,
                    aiModel = aiModel
                )
                _isSubmitting.value = false
                if (res.isSuccess) {
                    _isUploadSheetOpen.value = false
                    _editingVideo.value = null
                    showBanner(if (published) "Video Published Successfully" else "Video Saved as Draft")
                } else {
                    showBanner(res.exceptionOrNull()?.localizedMessage ?: "Failed to upload video")
                }
            } else {
                val updatedVideo = existing.copy(
                    title = title.trim(),
                    description = description.trim(),
                    videoUrl = videoUrl.trim(),
                    thumbnailUrl = thumbnailUrl.trim(),
                    prompt = prompt.trim(),
                    category = category,
                    published = published,
                    featured = featured,
                    popularPrompt = popularPrompt,
                    durationSeconds = durationSeconds,
                    aspectRatio = aspectRatio.trim().ifEmpty { "16:9" },
                    aiModel = aiModel.trim().ifEmpty { "Veo 2 Cinema" }
                )
                val res = repository.updateVideo(updatedVideo)
                _isSubmitting.value = false
                if (res.isSuccess) {
                    _isUploadSheetOpen.value = false
                    _editingVideo.value = null
                    if (_selectedVideoForDetail.value?.id == updatedVideo.id) {
                        _selectedVideoForDetail.value = updatedVideo
                    }
                    showBanner("Video Updated Successfully")
                } else {
                    showBanner(res.exceptionOrNull()?.localizedMessage ?: "Failed to update video")
                }
            }
        }
    }

    fun toggleVideoPublishStatus(video: VideoShowcase) {
        if (!_isAdmin.value) return
        viewModelScope.launch {
            val newStatus = !video.published
            val res = repository.setVideoPublishStatus(video, newStatus)
            if (res.isSuccess) {
                showBanner(if (newStatus) "Video Published Publicly" else "Video Unpublished (Draft)")
            } else {
                showBanner(res.exceptionOrNull()?.localizedMessage ?: "Failed to change publish status")
            }
        }
    }

    fun confirmDeleteVideo(video: VideoShowcase) {
        if (!_isAdmin.value) return
        viewModelScope.launch {
            _isSubmitting.value = true
            val res = repository.deleteVideo(video)
            _isSubmitting.value = false
            if (res.isSuccess) {
                if (_selectedVideoForDetail.value?.id == video.id) {
                    _selectedVideoForDetail.value = null
                }
                showBanner("Deleted \"${video.title}\"")
            } else {
                showBanner(res.exceptionOrNull()?.localizedMessage ?: "Failed to delete video")
            }
        }
    }

    fun toggleSavePrompt(video: VideoShowcase) {
        val currentSaved = (savedPromptsState.value as? UiState.Success)?.data ?: emptyList()
        val existing = currentSaved.firstOrNull { it.videoId == video.id }
        viewModelScope.launch {
            if (existing != null) {
                val res = repository.removeSavedPrompt(visitorId, existing.id)
                if (res.isSuccess) {
                    showBanner("Removed from Saved Prompts")
                }
            } else {
                val res = repository.savePromptForVisitor(visitorId, video)
                if (res.isSuccess) {
                    showBanner("Prompt Saved to Your Library")
                } else {
                    showBanner(res.exceptionOrNull()?.localizedMessage ?: "Failed to save prompt")
                }
            }
        }
    }

    fun updateSavedPromptNotes(savedPrompt: SavedPrompt, notes: String) {
        viewModelScope.launch {
            val res = repository.updateSavedPromptNotes(visitorId, savedPrompt, notes)
            if (res.isSuccess) {
                showBanner("Prompt Notes Updated")
            } else {
                showBanner(res.exceptionOrNull()?.localizedMessage ?: "Failed to update notes")
            }
        }
    }

    fun removeSavedPromptById(savedId: String) {
        viewModelScope.launch {
            val res = repository.removeSavedPrompt(visitorId, savedId)
            if (res.isSuccess) {
                showBanner("Prompt Removed from Library")
            }
        }
    }

    private companion object {
        const val TAG = "VishnuEditVM"
        const val STOP_TIMEOUT_MILLIS = 5000L
    }
}
