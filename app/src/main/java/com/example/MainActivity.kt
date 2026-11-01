package com.example

import android.content.Context
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.Category
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.outlined.BookmarkBorder
import androidx.compose.material.icons.outlined.Category
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.Companion.APPLICATION_KEY
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.data.repository.VideoRepository
import com.example.ui.screens.CategoriesScreen
import com.example.ui.screens.HomeScreen
import com.example.ui.screens.OwnerAdminLoginScreen
import com.example.ui.screens.OwnerAdminScreen
import com.example.ui.screens.ProfileScreen
import com.example.ui.screens.SavedPromptsScreen
import com.example.ui.screens.SearchScreen
import com.example.ui.screens.VideoDetailScreen
import com.example.ui.theme.DarkNavyElevated
import com.example.ui.theme.DarkNavySurface
import com.example.ui.theme.DeepBlackBg
import com.example.ui.theme.ElectricBlue
import com.example.ui.theme.ElectricBlueLight
import com.example.ui.theme.EmeraldSuccess
import com.example.ui.theme.GlassBorder
import com.example.ui.theme.MyApplicationTheme
import com.example.ui.theme.PureWhite
import com.example.ui.theme.SlateTextSecondary
import com.example.ui.viewmodel.MainTab
import com.example.ui.viewmodel.UiState
import com.example.ui.viewmodel.VishnuEditViewModel
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.delay
import java.util.UUID

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MyApplicationTheme {
                VishnuEditAppRoot()
            }
        }
    }
}

private fun getOrCreateVisitorId(context: Context): String {
    val prefs = context.getSharedPreferences("vishnu_edit_ai_visitor_prefs", Context.MODE_PRIVATE)
    val existing = prefs.getString("visitor_id", null)
    if (!existing.isNullOrBlank()) return existing
    val created = "visitor_${UUID.randomUUID().toString().replace("-", "_")}"
    prefs.edit().putString("visitor_id", created).apply()
    return created
}

private data class BottomNavSpec(
    val tab: MainTab,
    val label: String,
    val selectedIcon: ImageVector,
    val unselectedIcon: ImageVector,
    val testTag: String
)

/**
 * Directly opens the Home Screen for all public visitors without any user login or signup screen.
 */
@Composable
fun VishnuEditAppRoot() {
    val context = LocalContext.current
    val visitorId = remember { getOrCreateVisitorId(context) }

    val viewModel: VishnuEditViewModel = viewModel(
        key = visitorId,
        factory = viewModelFactory {
            initializer {
                val app = checkNotNull(this[APPLICATION_KEY]) {
                    "APPLICATION_KEY missing from CreationExtras"
                }
                val databaseId = app.getString(R.string.firestore_database_id)
                val db = FirebaseFirestore.getInstance(databaseId)
                VishnuEditViewModel(
                    repository = VideoRepository(db),
                    visitorId = visitorId
                )
            }
        }
    )

    val selectedTab by viewModel.selectedTab.collectAsStateWithLifecycle()
    val selectedCategory by viewModel.selectedCategory.collectAsStateWithLifecycle()
    val searchQuery by viewModel.searchQuery.collectAsStateWithLifecycle()
    val publishedVideosState by viewModel.publishedVideosState.collectAsStateWithLifecycle()
    val adminVideosState by viewModel.adminVideosState.collectAsStateWithLifecycle()
    val savedPromptsState by viewModel.savedPromptsState.collectAsStateWithLifecycle()
    val isAdmin by viewModel.isAdmin.collectAsStateWithLifecycle()
    val selectedVideoForDetail by viewModel.selectedVideoForDetail.collectAsStateWithLifecycle()
    val isAdminPortalOpen by viewModel.isAdminPortalOpen.collectAsStateWithLifecycle()
    val adminLoginError by viewModel.adminLoginError.collectAsStateWithLifecycle()
    val editingVideo by viewModel.editingVideo.collectAsStateWithLifecycle()
    val isUploadSheetOpen by viewModel.isUploadSheetOpen.collectAsStateWithLifecycle()
    val statusBanner by viewModel.statusBanner.collectAsStateWithLifecycle()
    val isSubmitting by viewModel.isSubmitting.collectAsStateWithLifecycle()

    // Auto-clear status toast/banner after 3 seconds
    LaunchedEffect(statusBanner) {
        if (statusBanner != null) {
            delay(3000L)
            viewModel.clearBanner()
        }
    }

    // BackHandler for non-Home bottom navigation tabs
    BackHandler(
        enabled = selectedVideoForDetail == null && !isAdminPortalOpen && selectedTab != MainTab.HOME
    ) {
        viewModel.selectTab(MainTab.HOME)
    }

    val savedPromptsList = (savedPromptsState as? UiState.Success)?.data ?: emptyList()
    val savedVideoIds = remember(savedPromptsList) {
        savedPromptsList.map { it.videoId }.toSet()
    }
    val publishedCount = ((publishedVideosState as? UiState.Success)?.data ?: emptyList()).size

    val navItems = remember {
        listOf(
            BottomNavSpec(
                tab = MainTab.HOME,
                label = "Home",
                selectedIcon = Icons.Filled.Home,
                unselectedIcon = Icons.Outlined.Home,
                testTag = "nav_tab_home"
            ),
            BottomNavSpec(
                tab = MainTab.CATEGORIES,
                label = "Categories",
                selectedIcon = Icons.Filled.Category,
                unselectedIcon = Icons.Outlined.Category,
                testTag = "nav_tab_categories"
            ),
            BottomNavSpec(
                tab = MainTab.SEARCH,
                label = "Search",
                selectedIcon = Icons.Filled.Search,
                unselectedIcon = Icons.Outlined.Search,
                testTag = "nav_tab_search"
            ),
            BottomNavSpec(
                tab = MainTab.SAVED_PROMPTS,
                label = "Saved Prompts",
                selectedIcon = Icons.Filled.Bookmark,
                unselectedIcon = Icons.Outlined.BookmarkBorder,
                testTag = "nav_tab_saved"
            ),
            BottomNavSpec(
                tab = MainTab.PROFILE,
                label = "Profile",
                selectedIcon = Icons.Filled.Person,
                unselectedIcon = Icons.Outlined.Person,
                testTag = "nav_tab_profile"
            )
        )
    }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        containerColor = DeepBlackBg,
        contentWindowInsets = WindowInsets.safeDrawing,
        bottomBar = {
            if (selectedVideoForDetail == null && !isAdminPortalOpen) {
                NavigationBar(
                    containerColor = DarkNavySurface,
                    tonalElevation = 8.dp,
                    windowInsets = WindowInsets.navigationBars,
                    modifier = Modifier
                        .fillMaxWidth()
                        .border(0.8.dp, GlassBorder, RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp))
                        .testTag("bottom_navigation_bar")
                ) {
                    navItems.forEach { item ->
                        val isSelected = selectedTab == item.tab
                        NavigationBarItem(
                            selected = isSelected,
                            onClick = { viewModel.selectTab(item.tab) },
                            icon = {
                                Icon(
                                    imageVector = if (isSelected) item.selectedIcon else item.unselectedIcon,
                                    contentDescription = item.label
                                )
                            },
                            label = {
                                Text(
                                    text = item.label,
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        fontSize = 10.sp,
                                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium
                                    ),
                                    maxLines = 1
                                )
                            },
                            colors = NavigationBarItemDefaults.colors(
                                selectedIconColor = PureWhite,
                                selectedTextColor = ElectricBlueLight,
                                indicatorColor = ElectricBlue.copy(alpha = 0.28f),
                                unselectedIconColor = SlateTextSecondary,
                                unselectedTextColor = SlateTextSecondary
                            ),
                            modifier = Modifier.testTag(item.testTag)
                        )
                    }
                }
            }
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            when {
                selectedVideoForDetail != null -> {
                    val video = selectedVideoForDetail!!
                    VideoDetailScreen(
                        video = video,
                        isSaved = savedVideoIds.contains(video.id),
                        isAdmin = isAdmin,
                        onBack = { viewModel.closeVideoDetail() },
                        onCopyPrompt = { viewModel.notifyPromptCopied() },
                        onToggleSave = { viewModel.toggleSavePrompt(video) },
                        onEditVideo = { target ->
                            viewModel.closeVideoDetail()
                            viewModel.startEditingVideo(target)
                        }
                    )
                }

                isAdminPortalOpen -> {
                    if (!isAdmin) {
                        OwnerAdminLoginScreen(
                            isSubmitting = isSubmitting,
                            errorMessage = adminLoginError,
                            onBack = { viewModel.closeAdminPortal() },
                            onClearError = { viewModel.clearAdminLoginError() },
                            onSubmitPassword = { password ->
                                viewModel.loginAdminWithPassword(password)
                            }
                        )
                    } else {
                        OwnerAdminScreen(
                            adminVideosState = adminVideosState,
                            editingVideo = editingVideo,
                            isUploadSheetOpen = isUploadSheetOpen,
                            isSubmitting = isSubmitting,
                            onBack = { viewModel.closeAdminPortal() },
                            onLogoutAdmin = { viewModel.logoutAdmin() },
                            onStartCreateNew = { viewModel.startCreateNewVideo() },
                            onStartEditVideo = { viewModel.startEditingVideo(it) },
                            onDismissForm = { viewModel.dismissUploadEditForm() },
                            onSaveVideoForm = { title, desc, vUrl, tUrl, prmpt, cat, pub, feat, pop, dur, ratio, model ->
                                viewModel.saveVideoForm(
                                    title = title,
                                    description = desc,
                                    videoUrl = vUrl,
                                    thumbnailUrl = tUrl,
                                    prompt = prmpt,
                                    category = cat,
                                    published = pub,
                                    featured = feat,
                                    popularPrompt = pop,
                                    durationSeconds = dur,
                                    aspectRatio = ratio,
                                    aiModel = model
                                )
                            },
                            onTogglePublish = { viewModel.toggleVideoPublishStatus(it) },
                            onDeleteVideo = { viewModel.confirmDeleteVideo(it) },
                            onSeedShowcase = { viewModel.seedShowcaseCatalog() },
                            onPreviewVideo = { viewModel.openVideoDetail(it) }
                        )
                    }
                }

                else -> {
                    when (selectedTab) {
                        MainTab.HOME -> {
                            HomeScreen(
                                videosState = publishedVideosState,
                                savedVideoIds = savedVideoIds,
                                isAdmin = isAdmin,
                                onOpenSearch = { q -> viewModel.openSearchWithQuery(q) },
                                onSelectCategory = { cat -> viewModel.selectCategoryAndOpen(cat) },
                                onOpenVideoDetail = { viewModel.openVideoDetail(it) },
                                onCopyPrompt = { viewModel.notifyPromptCopied() },
                                onToggleSavePrompt = { viewModel.toggleSavePrompt(it) },
                                onOpenAdminDashboard = { viewModel.openAdminPortal() },
                                onSeedShowcase = { viewModel.seedShowcaseCatalog() }
                            )
                        }

                        MainTab.CATEGORIES -> {
                            CategoriesScreen(
                                videosState = publishedVideosState,
                                selectedCategory = selectedCategory,
                                savedVideoIds = savedVideoIds,
                                onSelectCategory = { viewModel.selectCategoryAndOpen(it) },
                                onOpenVideoDetail = { viewModel.openVideoDetail(it) },
                                onCopyPrompt = { viewModel.notifyPromptCopied() },
                                onToggleSavePrompt = { viewModel.toggleSavePrompt(it) }
                            )
                        }

                        MainTab.SEARCH -> {
                            SearchScreen(
                                videosState = publishedVideosState,
                                searchQuery = searchQuery,
                                savedVideoIds = savedVideoIds,
                                onSearchQueryChange = { viewModel.updateSearchQuery(it) },
                                onOpenVideoDetail = { viewModel.openVideoDetail(it) },
                                onCopyPrompt = { viewModel.notifyPromptCopied() },
                                onToggleSavePrompt = { viewModel.toggleSavePrompt(it) }
                            )
                        }

                        MainTab.SAVED_PROMPTS -> {
                            SavedPromptsScreen(
                                savedPromptsState = savedPromptsState,
                                onCopyPrompt = { viewModel.notifyPromptCopied() },
                                onUpdateNotes = { saved, notes ->
                                    viewModel.updateSavedPromptNotes(saved, notes)
                                },
                                onRemoveSavedPrompt = { id ->
                                    viewModel.removeSavedPromptById(id)
                                }
                            )
                        }

                        MainTab.PROFILE -> {
                            ProfileScreen(
                                isAdmin = isAdmin,
                                savedPrompts = savedPromptsList,
                                totalPublishedCount = publishedCount,
                                onOpenAdminPortal = { viewModel.openAdminPortal() },
                                onLogoutAdmin = { viewModel.logoutAdmin() },
                                onNavigateToSavedPrompts = {
                                    viewModel.selectTab(MainTab.SAVED_PROMPTS)
                                }
                            )
                        }
                    }
                }
            }

            // Floating Status Toast Banner ("Prompt Copied Successfully" & Admin actions)
            AnimatedVisibility(
                visible = statusBanner != null,
                enter = slideInVertically(initialOffsetY = { -it }) + fadeIn(),
                exit = slideOutVertically(targetOffsetY = { -it }) + fadeOut(),
                modifier = Modifier
                    .align(Alignment.TopCenter)
                    .padding(horizontal = 20.dp, vertical = 12.dp)
            ) {
                Surface(
                    color = DarkNavyElevated,
                    shape = RoundedCornerShape(16.dp),
                    shadowElevation = 12.dp,
                    modifier = Modifier
                        .border(1.5.dp, ElectricBlue, RoundedCornerShape(16.dp))
                        .testTag("status_banner_toast")
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 18.dp, vertical = 12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.CheckCircle,
                            contentDescription = null,
                            tint = EmeraldSuccess,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(10.dp))
                        Text(
                            text = statusBanner ?: "",
                            style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                            color = PureWhite
                        )
                    }
                }
            }
        }
    }
}
