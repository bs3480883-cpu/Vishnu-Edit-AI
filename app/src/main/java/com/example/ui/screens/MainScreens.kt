package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AdminPanelSettings
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.BookmarkRemove
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.CloudUpload
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.EditNote
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material.icons.filled.MovieFilter
import androidx.compose.material.icons.filled.Pets
import androidx.compose.material.icons.filled.PlayCircleFilled
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.SmartDisplay
import androidx.compose.material.icons.filled.ViewInAr
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.SavedPrompt
import com.example.data.model.VideoCategory
import com.example.data.model.VideoShowcase
import com.example.ui.components.FeaturedVideoCard
import com.example.ui.components.GradientActionButton
import com.example.ui.components.VideoShowcaseCard
import com.example.ui.components.VishnuBrandLogo
import com.example.ui.components.copyPromptToClipboard
import com.example.ui.theme.DarkNavyElevated
import com.example.ui.theme.DarkNavySurface
import com.example.ui.theme.DeepBlackBg
import com.example.ui.theme.ElectricBlue
import com.example.ui.theme.ElectricBlueLight
import com.example.ui.theme.GlassBorder
import com.example.ui.theme.JetBrainsMonoFontFamily
import com.example.ui.theme.NeonPurple
import com.example.ui.theme.NeonPurpleLight
import com.example.ui.theme.PureWhite
import com.example.ui.theme.PurpleGlassBorder
import com.example.ui.theme.SlateTextSecondary
import com.example.ui.theme.VishnuCardGlassBrush
import com.example.ui.theme.VishnuPrimaryGradient
import com.example.ui.viewmodel.UiState

fun categoryIcon(cat: VideoCategory): ImageVector {
    return when (cat) {
        VideoCategory.CINEMATIC -> Icons.Default.MovieFilter
        VideoCategory.ANIMAL -> Icons.Default.Pets
        VideoCategory.REALISTIC -> Icons.Default.Visibility
        VideoCategory.ANIMATION_3D -> Icons.Default.ViewInAr
        VideoCategory.VFX -> Icons.Default.AutoAwesome
        VideoCategory.SHORT_FILMS -> Icons.Default.SmartDisplay
    }
}

/**
 * 1. HOME SCREEN:
 * - Professional app header
 * - VISHNU EDIT AI logo
 * - Search bar
 * - Featured AI videos section
 * - Video category section
 * - Popular AI prompts section
 * - Latest uploads section
 */
@Composable
fun HomeScreen(
    videosState: UiState<List<VideoShowcase>>,
    savedVideoIds: Set<String>,
    isAdmin: Boolean,
    onOpenSearch: (String) -> Unit,
    onSelectCategory: (String) -> Unit,
    onOpenVideoDetail: (VideoShowcase) -> Unit,
    onCopyPrompt: () -> Unit,
    onToggleSavePrompt: (VideoShowcase) -> Unit,
    onOpenAdminDashboard: () -> Unit,
    onSeedShowcase: () -> Unit
) {
    val context = LocalContext.current
    var homeSearchInput by remember { mutableStateOf("") }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(DeepBlackBg)
            .testTag("home_screen_list"),
        contentPadding = PaddingValues(bottom = 104.dp)
    ) {
        // 1. Professional App Header + 2. VISHNU EDIT AI Logo
        item {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        Brush.verticalGradient(
                            colors = listOf(
                                ElectricBlue.copy(alpha = 0.14f),
                                NeonPurple.copy(alpha = 0.08f),
                                DeepBlackBg
                            )
                        )
                    )
                    .padding(horizontal = 20.dp, vertical = 18.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    VishnuBrandLogo(
                        showSubtitle = true,
                        logoSize = 46.dp,
                        compactHorizontal = true
                    )

                    if (isAdmin) {
                        Surface(
                            color = DarkNavyElevated,
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier
                                .border(1.dp, ElectricBlue, RoundedCornerShape(12.dp))
                                .clickable { onOpenAdminDashboard() }
                                .testTag("header_admin_studio_btn")
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = Icons.Default.AdminPanelSettings,
                                    contentDescription = "Owner Admin Studio",
                                    tint = ElectricBlueLight,
                                    modifier = Modifier.size(18.dp)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = "ADMIN",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = PureWhite
                                )
                            }
                        }
                    } else {
                        Surface(
                            color = DarkNavyElevated.copy(alpha = 0.8f),
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier.border(0.8.dp, GlassBorder, RoundedCornerShape(10.dp))
                        ) {
                            Text(
                                text = "PUBLIC SHOWCASE",
                                style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp),
                                color = ElectricBlueLight,
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(18.dp))

                // 3. Search Bar on Home Screen
                OutlinedTextField(
                    value = homeSearchInput,
                    onValueChange = {
                        homeSearchInput = it
                        if (it.length >= 2) {
                            onOpenSearch(it)
                        }
                    },
                    placeholder = {
                        Text(
                            text = "Search AI videos, prompts, models, or styles…",
                            color = SlateTextSecondary,
                            style = MaterialTheme.typography.bodyMedium
                        )
                    },
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Default.Search,
                            contentDescription = "Search",
                            tint = ElectricBlueLight
                        )
                    },
                    trailingIcon = {
                        if (homeSearchInput.isNotEmpty()) {
                            IconButton(onClick = { homeSearchInput = "" }) {
                                Icon(
                                    imageVector = Icons.Default.Clear,
                                    contentDescription = "Clear",
                                    tint = SlateTextSecondary
                                )
                            }
                        }
                    },
                    singleLine = true,
                    shape = RoundedCornerShape(16.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedContainerColor = DarkNavySurface,
                        unfocusedContainerColor = DarkNavySurface,
                        focusedBorderColor = ElectricBlue,
                        unfocusedBorderColor = GlassBorder,
                        focusedTextColor = PureWhite,
                        unfocusedTextColor = PureWhite
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("home_search_bar")
                )
            }
        }

        // 4. Video Category Section (All 6 Categories)
        item {
            SectionHeader(
                title = "Video Categories",
                subtitle = "Explore 6 specialized AI generation domains",
                actionLabel = "View All",
                onActionClick = { onSelectCategory(VideoCategory.CINEMATIC.displayName) }
            )

            LazyRow(
                contentPadding = PaddingValues(horizontal = 20.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(VideoCategory.entries) { cat ->
                    CategoryPillCard(
                        category = cat,
                        onClick = { onSelectCategory(cat.displayName) }
                    )
                }
            }
            Spacer(modifier = Modifier.height(22.dp))
        }

        when (videosState) {
            is UiState.Loading -> {
                item {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 64.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            CircularProgressIndicator(color = ElectricBlue)
                            Spacer(modifier = Modifier.height(12.dp))
                            Text(
                                text = "Loading AI Video Showcase…",
                                style = MaterialTheme.typography.bodyMedium,
                                color = SlateTextSecondary
                            )
                        }
                    }
                }
            }

            is UiState.Error -> {
                item {
                    Surface(
                        color = DarkNavySurface,
                        shape = RoundedCornerShape(16.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 20.dp)
                            .border(1.dp, MaterialTheme.colorScheme.error, RoundedCornerShape(16.dp))
                    ) {
                        Column(
                            modifier = Modifier.padding(20.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                text = "Unable to load showcase",
                                style = MaterialTheme.typography.titleMedium,
                                color = PureWhite
                            )
                            Spacer(modifier = Modifier.height(6.dp))
                            Text(
                                text = videosState.message,
                                style = MaterialTheme.typography.bodySmall,
                                color = SlateTextSecondary,
                                textAlign = TextAlign.Center
                            )
                        }
                    }
                }
            }

            is UiState.Success -> {
                val videos = videosState.data
                if (videos.isEmpty()) {
                    item {
                        EmptyShowcaseCard(
                            isAdmin = isAdmin,
                            onSeedShowcase = onSeedShowcase,
                            onOpenAdmin = onOpenAdminDashboard
                        )
                    }
                } else {
                    val featuredVideos = videos.filter { it.featured }.ifEmpty { videos.take(4) }
                    val popularPrompts = videos.filter { it.popularPrompt }.ifEmpty { videos.take(4) }

                    // 5. Featured AI Videos Section
                    item {
                        SectionHeader(
                            title = "Featured AI Videos",
                            subtitle = "Curated high-bitrate studio renders & cinema sequences"
                        )
                        LazyRow(
                            contentPadding = PaddingValues(horizontal = 20.dp),
                            horizontalArrangement = Arrangement.spacedBy(16.dp)
                        ) {
                            items(featuredVideos, key = { "feat_${it.id}" }) { video ->
                                FeaturedVideoCard(
                                    video = video,
                                    isSaved = savedVideoIds.contains(video.id),
                                    onVideoClick = { onOpenVideoDetail(video) },
                                    onCopyPrompt = onCopyPrompt,
                                    onToggleSave = { onToggleSavePrompt(video) }
                                )
                            }
                        }
                        Spacer(modifier = Modifier.height(26.dp))
                    }

                    // 6. Popular AI Prompts Section
                    item {
                        SectionHeader(
                            title = "Popular AI Prompts",
                            subtitle = "Production-tested prompts ready to copy into your AI tools"
                        )
                        LazyRow(
                            contentPadding = PaddingValues(horizontal = 20.dp),
                            horizontalArrangement = Arrangement.spacedBy(14.dp)
                        ) {
                            items(popularPrompts, key = { "pop_${it.id}" }) { video ->
                                PopularPromptCompactCard(
                                    video = video,
                                    onOpenVideo = { onOpenVideoDetail(video) },
                                    onCopyPrompt = {
                                        copyPromptToClipboard(context, video.prompt, onCopyPrompt)
                                    }
                                )
                            }
                        }
                        Spacer(modifier = Modifier.height(26.dp))
                    }

                    // 7. Latest Uploads Section
                    item {
                        SectionHeader(
                            title = "Latest Uploads",
                            subtitle = "Complete video showcase with one-tap prompt copying"
                        )
                    }

                    items(videos, key = { "latest_${it.id}" }) { video ->
                        VideoShowcaseCard(
                            video = video,
                            isSaved = savedVideoIds.contains(video.id),
                            onVideoClick = { onOpenVideoDetail(video) },
                            onCopyPrompt = onCopyPrompt,
                            onToggleSave = { onToggleSavePrompt(video) },
                            modifier = Modifier.padding(horizontal = 20.dp, vertical = 8.dp)
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun SectionHeader(
    title: String,
    subtitle: String,
    actionLabel: String? = null,
    onActionClick: (() -> Unit)? = null
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp, vertical = 10.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = title,
                style = MaterialTheme.typography.headlineSmall,
                color = PureWhite
            )
            Text(
                text = subtitle,
                style = MaterialTheme.typography.bodySmall,
                color = SlateTextSecondary
            )
        }
        if (actionLabel != null && onActionClick != null) {
            TextButton(onClick = onActionClick) {
                Text(
                    text = actionLabel,
                    style = MaterialTheme.typography.labelLarge,
                    color = ElectricBlueLight
                )
            }
        }
    }
}

@Composable
private fun CategoryPillCard(
    category: VideoCategory,
    onClick: () -> Unit
) {
    Surface(
        modifier = Modifier
            .width(175.dp)
            .clip(RoundedCornerShape(16.dp))
            .border(1.dp, GlassBorder, RoundedCornerShape(16.dp))
            .clickable { onClick() }
            .testTag("category_card_${category.name}"),
        color = DarkNavySurface,
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Box(
                modifier = Modifier
                    .size(38.dp)
                    .clip(RoundedCornerShape(10.dp))
                    .background(VishnuPrimaryGradient),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = categoryIcon(category),
                    contentDescription = category.displayName,
                    tint = PureWhite,
                    modifier = Modifier.size(20.dp)
                )
            }
            Spacer(modifier = Modifier.height(10.dp))
            Text(
                text = category.displayName,
                style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                color = PureWhite,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = category.subtitle,
                style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.sp),
                color = SlateTextSecondary,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}

@Composable
private fun PopularPromptCompactCard(
    video: VideoShowcase,
    onOpenVideo: () -> Unit,
    onCopyPrompt: () -> Unit
) {
    Surface(
        modifier = Modifier
            .width(290.dp)
            .clip(RoundedCornerShape(18.dp))
            .border(1.dp, PurpleGlassBorder, RoundedCornerShape(18.dp))
            .clickable { onOpenVideo() },
        color = DarkNavySurface,
        shape = RoundedCornerShape(18.dp)
    ) {
        Column(
            modifier = Modifier
                .background(VishnuCardGlassBrush)
                .padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Surface(
                    color = NeonPurple.copy(alpha = 0.22f),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text(
                        text = video.aiModel,
                        style = MaterialTheme.typography.labelSmall,
                        color = NeonPurpleLight,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }
                Text(
                    text = video.category,
                    style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp),
                    color = ElectricBlueLight
                )
            }
            Spacer(modifier = Modifier.height(10.dp))
            Text(
                text = video.title,
                style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                color = PureWhite,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            Spacer(modifier = Modifier.height(8.dp))
            Surface(
                color = DeepBlackBg.copy(alpha = 0.9f),
                shape = RoundedCornerShape(10.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = video.prompt,
                    style = MaterialTheme.typography.bodySmall.copy(
                        fontFamily = JetBrainsMonoFontFamily,
                        fontSize = 11.5.sp
                    ),
                    color = PureWhite.copy(alpha = 0.9f),
                    maxLines = 3,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.padding(10.dp)
                )
            }
            Spacer(modifier = Modifier.height(12.dp))
            GradientActionButton(
                text = "COPY PROMPT",
                icon = Icons.Default.ContentCopy,
                onClick = onCopyPrompt,
                modifier = Modifier.fillMaxWidth(),
                testTag = "copy_popular_prompt_${video.id}"
            )
        }
    }
}

@Composable
private fun EmptyShowcaseCard(
    isAdmin: Boolean,
    onSeedShowcase: () -> Unit,
    onOpenAdmin: () -> Unit
) {
    Surface(
        color = DarkNavySurface,
        shape = RoundedCornerShape(22.dp),
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp, vertical = 16.dp)
            .border(1.dp, GlassBorder, RoundedCornerShape(22.dp))
    ) {
        Column(
            modifier = Modifier.padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(
                modifier = Modifier
                    .size(64.dp)
                    .clip(CircleShape)
                    .background(VishnuPrimaryGradient),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.PlayCircleFilled,
                    contentDescription = null,
                    tint = PureWhite,
                    modifier = Modifier.size(34.dp)
                )
            }
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = "Studio Showcase Ready",
                style = MaterialTheme.typography.headlineSmall,
                color = PureWhite
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = if (isAdmin) {
                    "You are signed in as the Studio Owner. Initialize the 6 production AI video showcases or upload a custom AI video from the Admin Studio."
                } else {
                    "No published AI videos are live right now. Only the authorized studio owner can publish showcase content."
                },
                style = MaterialTheme.typography.bodyMedium,
                color = SlateTextSecondary,
                textAlign = TextAlign.Center
            )
            if (isAdmin) {
                Spacer(modifier = Modifier.height(18.dp))
                GradientActionButton(
                    text = "Initialize 6 Studio AI Videos",
                    icon = Icons.Default.AutoAwesome,
                    onClick = onSeedShowcase,
                    modifier = Modifier.fillMaxWidth(),
                    testTag = "seed_showcase_button"
                )
                Spacer(modifier = Modifier.height(10.dp))
                TextButton(onClick = onOpenAdmin) {
                    Icon(
                        imageVector = Icons.Default.CloudUpload,
                        contentDescription = null,
                        tint = ElectricBlueLight,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "Open Owner Admin Dashboard",
                        color = ElectricBlueLight,
                        style = MaterialTheme.typography.labelLarge
                    )
                }
            }
        }
    }
}

/**
 * 2. CATEGORIES SCREEN:
 * Interactive filter across all 6 required AI video categories.
 */
@Composable
fun CategoriesScreen(
    videosState: UiState<List<VideoShowcase>>,
    selectedCategory: String?,
    savedVideoIds: Set<String>,
    onSelectCategory: (String?) -> Unit,
    onOpenVideoDetail: (VideoShowcase) -> Unit,
    onCopyPrompt: () -> Unit,
    onToggleSavePrompt: (VideoShowcase) -> Unit
) {
    val allVideos = (videosState as? UiState.Success)?.data ?: emptyList()
    val filteredVideos = remember(allVideos, selectedCategory) {
        if (selectedCategory.isNullOrBlank()) {
            allVideos
        } else {
            allVideos.filter { it.category.equals(selectedCategory, ignoreCase = true) }
        }
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(DeepBlackBg)
            .testTag("categories_screen_list"),
        contentPadding = PaddingValues(bottom = 104.dp)
    ) {
        item {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 18.dp)
            ) {
                Text(
                    text = "VIDEO CATEGORIES",
                    style = MaterialTheme.typography.labelSmall,
                    color = ElectricBlueLight
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Explore by AI Genre",
                    style = MaterialTheme.typography.headlineMedium,
                    color = PureWhite
                )
                Spacer(modifier = Modifier.height(14.dp))

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    FilterChip(
                        selected = selectedCategory == null,
                        onClick = { onSelectCategory(null) },
                        label = { Text("All (${allVideos.size})") },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = ElectricBlue,
                            selectedLabelColor = PureWhite,
                            containerColor = DarkNavySurface,
                            labelColor = SlateTextSecondary
                        )
                    )
                    VideoCategory.entries.forEach { cat ->
                        val count = allVideos.count { it.category.equals(cat.displayName, ignoreCase = true) }
                        FilterChip(
                            selected = selectedCategory.equals(cat.displayName, ignoreCase = true),
                            onClick = { onSelectCategory(cat.displayName) },
                            label = { Text("${cat.displayName} ($count)") },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = ElectricBlue,
                                selectedLabelColor = PureWhite,
                                containerColor = DarkNavySurface,
                                labelColor = SlateTextSecondary
                            ),
                            modifier = Modifier.testTag("filter_chip_${cat.name}")
                        )
                    }
                }
            }
        }

        if (filteredVideos.isEmpty()) {
            item {
                Surface(
                    color = DarkNavySurface,
                    shape = RoundedCornerShape(18.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp, vertical = 24.dp)
                        .border(1.dp, GlassBorder, RoundedCornerShape(18.dp))
                ) {
                    Column(
                        modifier = Modifier.padding(28.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Icon(
                            imageVector = Icons.Default.MovieFilter,
                            contentDescription = null,
                            tint = ElectricBlueLight,
                            modifier = Modifier.size(42.dp)
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        Text(
                            text = "No videos in ${selectedCategory ?: "this category"} yet",
                            style = MaterialTheme.typography.titleMedium,
                            color = PureWhite
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = "Switch to another category or check back when the owner publishes new AI videos.",
                            style = MaterialTheme.typography.bodySmall,
                            color = SlateTextSecondary,
                            textAlign = TextAlign.Center
                        )
                    }
                }
            }
        } else {
            items(filteredVideos, key = { "cat_vid_${it.id}" }) { video ->
                VideoShowcaseCard(
                    video = video,
                    isSaved = savedVideoIds.contains(video.id),
                    onVideoClick = { onOpenVideoDetail(video) },
                    onCopyPrompt = onCopyPrompt,
                    onToggleSave = { onToggleSavePrompt(video) },
                    modifier = Modifier.padding(horizontal = 20.dp, vertical = 8.dp)
                )
            }
        }
    }
}

/**
 * 3. SEARCH SCREEN:
 * Full-text search across video titles, descriptions, AI prompts, models, and categories.
 */
@Composable
fun SearchScreen(
    videosState: UiState<List<VideoShowcase>>,
    searchQuery: String,
    savedVideoIds: Set<String>,
    onSearchQueryChange: (String) -> Unit,
    onOpenVideoDetail: (VideoShowcase) -> Unit,
    onCopyPrompt: () -> Unit,
    onToggleSavePrompt: (VideoShowcase) -> Unit
) {
    val allVideos = (videosState as? UiState.Success)?.data ?: emptyList()
    val matchingVideos = remember(allVideos, searchQuery) {
        val q = searchQuery.trim().lowercase()
        if (q.isEmpty()) {
            allVideos
        } else {
            allVideos.filter {
                it.title.lowercase().contains(q) ||
                    it.description.lowercase().contains(q) ||
                    it.prompt.lowercase().contains(q) ||
                    it.category.lowercase().contains(q) ||
                    it.aiModel.lowercase().contains(q)
            }
        }
    }

    val quickTags = listOf("Cyberpunk", "Snow Leopard", "Veo 2", "Sora", "3D", "VFX", "8k", "Anamorphic")

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(DeepBlackBg)
            .testTag("search_screen_list"),
        contentPadding = PaddingValues(bottom = 104.dp)
    ) {
        item {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 18.dp)
            ) {
                Text(
                    text = "PROMPT & VIDEO SEARCH",
                    style = MaterialTheme.typography.labelSmall,
                    color = ElectricBlueLight
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Search AI Showcase",
                    style = MaterialTheme.typography.headlineMedium,
                    color = PureWhite
                )
                Spacer(modifier = Modifier.height(14.dp))

                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = onSearchQueryChange,
                    placeholder = {
                        Text(
                            text = "Search by title, prompt keywords, camera lens, or AI model…",
                            color = SlateTextSecondary
                        )
                    },
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Default.Search,
                            contentDescription = "Search",
                            tint = ElectricBlueLight
                        )
                    },
                    trailingIcon = {
                        if (searchQuery.isNotEmpty()) {
                            IconButton(onClick = { onSearchQueryChange("") }) {
                                Icon(
                                    imageVector = Icons.Default.Clear,
                                    contentDescription = "Clear search",
                                    tint = SlateTextSecondary
                                )
                            }
                        }
                    },
                    singleLine = true,
                    shape = RoundedCornerShape(16.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedContainerColor = DarkNavySurface,
                        unfocusedContainerColor = DarkNavySurface,
                        focusedBorderColor = ElectricBlue,
                        unfocusedBorderColor = GlassBorder,
                        focusedTextColor = PureWhite,
                        unfocusedTextColor = PureWhite
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("search_screen_input")
                )

                Spacer(modifier = Modifier.height(12.dp))

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    quickTags.forEach { tag ->
                        FilterChip(
                            selected = searchQuery.equals(tag, ignoreCase = true),
                            onClick = {
                                if (searchQuery.equals(tag, ignoreCase = true)) {
                                    onSearchQueryChange("")
                                } else {
                                    onSearchQueryChange(tag)
                                }
                            },
                            label = { Text("#$tag") },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = NeonPurple,
                                selectedLabelColor = PureWhite,
                                containerColor = DarkNavySurface,
                                labelColor = SlateTextSecondary
                            )
                        )
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "${matchingVideos.size} matching AI videos & prompts",
                    style = MaterialTheme.typography.labelMedium,
                    color = SlateTextSecondary
                )
            }
        }

        if (matchingVideos.isEmpty()) {
            item {
                Surface(
                    color = DarkNavySurface,
                    shape = RoundedCornerShape(18.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp, vertical = 20.dp)
                        .border(1.dp, GlassBorder, RoundedCornerShape(18.dp))
                ) {
                    Column(
                        modifier = Modifier.padding(28.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Icon(
                            imageVector = Icons.Default.FilterList,
                            contentDescription = null,
                            tint = NeonPurpleLight,
                            modifier = Modifier.size(40.dp)
                        )
                        Spacer(modifier = Modifier.height(10.dp))
                        Text(
                            text = "No matching videos or prompts",
                            style = MaterialTheme.typography.titleMedium,
                            color = PureWhite
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = "Try searching for another style, lens, or AI model.",
                            style = MaterialTheme.typography.bodySmall,
                            color = SlateTextSecondary,
                            textAlign = TextAlign.Center
                        )
                    }
                }
            }
        } else {
            items(matchingVideos, key = { "search_${it.id}" }) { video ->
                VideoShowcaseCard(
                    video = video,
                    isSaved = savedVideoIds.contains(video.id),
                    onVideoClick = { onOpenVideoDetail(video) },
                    onCopyPrompt = onCopyPrompt,
                    onToggleSave = { onToggleSavePrompt(video) },
                    modifier = Modifier.padding(horizontal = 20.dp, vertical = 8.dp)
                )
            }
        }
    }
}

/**
 * 4. SAVED PROMPTS SCREEN:
 * Cloud-persisted personal library of bookmarked AI video generation prompts.
 */
@Composable
fun SavedPromptsScreen(
    savedPromptsState: UiState<List<SavedPrompt>>,
    onCopyPrompt: () -> Unit,
    onUpdateNotes: (SavedPrompt, String) -> Unit,
    onRemoveSavedPrompt: (String) -> Unit
) {
    val context = LocalContext.current
    var editingNotesTarget by remember { mutableStateOf<SavedPrompt?>(null) }
    var notesDraft by remember { mutableStateOf("") }

    if (editingNotesTarget != null) {
        val target = editingNotesTarget!!
        AlertDialog(
            onDismissRequest = { editingNotesTarget = null },
            containerColor = DarkNavySurface,
            titleContentColor = PureWhite,
            textContentColor = PureWhite,
            title = {
                Text(
                    text = "Prompt Studio Notes",
                    style = MaterialTheme.typography.titleLarge
                )
            },
            text = {
                Column {
                    Text(
                        text = target.videoTitle,
                        style = MaterialTheme.typography.labelMedium,
                        color = ElectricBlueLight
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    OutlinedTextField(
                        value = notesDraft,
                        onValueChange = { notesDraft = it },
                        label = { Text("Personal Remix Notes (aspect ratio, seed, camera)") },
                        maxLines = 4,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = ElectricBlue,
                            unfocusedBorderColor = GlassBorder,
                            focusedTextColor = PureWhite,
                            unfocusedTextColor = PureWhite
                        ),
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            },
            confirmButton = {
                GradientActionButton(
                    text = "Save Notes",
                    onClick = {
                        onUpdateNotes(target, notesDraft)
                        editingNotesTarget = null
                    }
                )
            },
            dismissButton = {
                TextButton(onClick = { editingNotesTarget = null }) {
                    Text("Cancel", color = SlateTextSecondary)
                }
            }
        )
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(DeepBlackBg)
            .testTag("saved_prompts_screen_list"),
        contentPadding = PaddingValues(bottom = 104.dp)
    ) {
        item {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 18.dp)
            ) {
                Text(
                    text = "PERSONAL CLOUD LIBRARY",
                    style = MaterialTheme.typography.labelSmall,
                    color = NeonPurpleLight
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Saved AI Prompts",
                    style = MaterialTheme.typography.headlineMedium,
                    color = PureWhite
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Bookmarked prompts synced to your account for instant copying into your AI video workflow.",
                    style = MaterialTheme.typography.bodySmall,
                    color = SlateTextSecondary
                )
            }
        }

        when (savedPromptsState) {
            is UiState.Loading -> {
                item {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 48.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator(color = ElectricBlue)
                    }
                }
            }

            is UiState.Error -> {
                item {
                    Text(
                        text = savedPromptsState.message,
                        color = MaterialTheme.colorScheme.error,
                        modifier = Modifier.padding(20.dp)
                    )
                }
            }

            is UiState.Success -> {
                val list = savedPromptsState.data
                if (list.isEmpty()) {
                    item {
                        Surface(
                            color = DarkNavySurface,
                            shape = RoundedCornerShape(20.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 20.dp, vertical = 20.dp)
                                .border(1.dp, GlassBorder, RoundedCornerShape(20.dp))
                        ) {
                            Column(
                                modifier = Modifier.padding(28.dp),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Icon(
                                    imageVector = Icons.Default.AutoAwesome,
                                    contentDescription = null,
                                    tint = NeonPurpleLight,
                                    modifier = Modifier.size(42.dp)
                                )
                                Spacer(modifier = Modifier.height(12.dp))
                                Text(
                                    text = "No Saved Prompts Yet",
                                    style = MaterialTheme.typography.titleMedium,
                                    color = PureWhite
                                )
                                Spacer(modifier = Modifier.height(6.dp))
                                Text(
                                    text = "Tap the bookmark icon on any video in the Home or Categories showcase to save its AI prompt here.",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = SlateTextSecondary,
                                    textAlign = TextAlign.Center
                                )
                            }
                        }
                    }
                } else {
                    items(list, key = { "saved_${it.id}" }) { saved ->
                        Surface(
                            color = DarkNavySurface,
                            shape = RoundedCornerShape(18.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 20.dp, vertical = 8.dp)
                                .border(1.dp, PurpleGlassBorder, RoundedCornerShape(18.dp))
                        ) {
                            Column(
                                modifier = Modifier
                                    .background(VishnuCardGlassBrush)
                                    .padding(16.dp)
                            ) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Surface(
                                        color = ElectricBlue.copy(alpha = 0.2f),
                                        shape = RoundedCornerShape(8.dp)
                                    ) {
                                        Text(
                                            text = "${saved.category} • ${saved.aiModel}",
                                            style = MaterialTheme.typography.labelSmall,
                                            color = ElectricBlueLight,
                                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                        )
                                    }

                                    Row {
                                        IconButton(
                                            onClick = {
                                                notesDraft = saved.notes
                                                editingNotesTarget = saved
                                            },
                                            modifier = Modifier.size(36.dp)
                                        ) {
                                            Icon(
                                                imageVector = Icons.Default.EditNote,
                                                contentDescription = "Edit Notes",
                                                tint = SlateTextSecondary
                                            )
                                        }
                                        IconButton(
                                            onClick = { onRemoveSavedPrompt(saved.id) },
                                            modifier = Modifier.size(36.dp)
                                        ) {
                                            Icon(
                                                imageVector = Icons.Default.BookmarkRemove,
                                                contentDescription = "Remove Saved Prompt",
                                                tint = MaterialTheme.colorScheme.error
                                            )
                                        }
                                    }
                                }

                                Spacer(modifier = Modifier.height(8.dp))
                                Text(
                                    text = saved.videoTitle,
                                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                                    color = PureWhite
                                )
                                Spacer(modifier = Modifier.height(10.dp))

                                Surface(
                                    color = DeepBlackBg,
                                    shape = RoundedCornerShape(12.dp),
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .border(1.dp, GlassBorder, RoundedCornerShape(12.dp))
                                ) {
                                    Text(
                                        text = saved.prompt,
                                        style = MaterialTheme.typography.bodySmall.copy(
                                            fontFamily = JetBrainsMonoFontFamily,
                                            lineHeight = 18.sp
                                        ),
                                        color = PureWhite.copy(alpha = 0.92f),
                                        modifier = Modifier.padding(12.dp)
                                    )
                                }

                                if (saved.notes.isNotBlank()) {
                                    Spacer(modifier = Modifier.height(8.dp))
                                    Text(
                                        text = "Studio Note: ${saved.notes}",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = NeonPurpleLight
                                    )
                                }

                                Spacer(modifier = Modifier.height(12.dp))
                                GradientActionButton(
                                    text = "COPY PROMPT",
                                    icon = Icons.Default.ContentCopy,
                                    onClick = {
                                        copyPromptToClipboard(context, saved.prompt, onCopyPrompt)
                                    },
                                    modifier = Modifier.fillMaxWidth(),
                                    testTag = "copy_saved_prompt_${saved.id}"
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
