package com.example.ui.screens

import android.content.Intent
import androidx.activity.compose.BackHandler
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
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
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.AddCircle
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.CloudUpload
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Image
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Movie
import androidx.compose.material.icons.filled.Public
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
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
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import coil.compose.AsyncImage
import com.example.data.model.StudioMediaCatalog
import com.example.data.model.VideoCategory
import com.example.data.model.VideoShowcase
import com.example.ui.components.GradientActionButton
import com.example.ui.components.VishnuBrandLogo
import com.example.ui.theme.AmberWarning
import com.example.ui.theme.CrimsonDanger
import com.example.ui.theme.DarkNavyElevated
import com.example.ui.theme.DarkNavySurface
import com.example.ui.theme.DeepBlackBg
import com.example.ui.theme.ElectricBlue
import com.example.ui.theme.ElectricBlueLight
import com.example.ui.theme.EmeraldSuccess
import com.example.ui.theme.GlassBorder
import com.example.ui.theme.JetBrainsMonoFontFamily
import com.example.ui.theme.NeonPurple
import com.example.ui.theme.NeonPurpleLight
import com.example.ui.theme.PureWhite
import com.example.ui.theme.PurpleGlassBorder
import com.example.ui.theme.SlateTextSecondary
import com.example.ui.theme.VishnuCardGlassBrush
import com.example.ui.viewmodel.UiState

enum class AdminContentFilter(val label: String) {
    ALL("All Content"),
    PUBLISHED("Published"),
    DRAFTS("Unpublished Drafts")
}

/**
 * PRIVATE OWNER ADMIN PASSWORD LOGIN SCREEN:
 * Separate secure password-based login exclusively for the Owner.
 * Never displayed as a popup to public users.
 */
@Composable
fun OwnerAdminLoginScreen(
    isSubmitting: Boolean,
    errorMessage: String?,
    onBack: () -> Unit,
    onClearError: () -> Unit,
    onSubmitPassword: (String) -> Unit
) {
    BackHandler { onBack() }
    var password by remember { mutableStateOf("") }
    var showPassword by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(DeepBlackBg)
            .verticalScroll(rememberScrollState())
            .padding(24.dp)
            .testTag("owner_admin_login_screen"),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(
                onClick = onBack,
                modifier = Modifier
                    .size(44.dp)
                    .clip(CircleShape)
                    .background(DarkNavySurface)
                    .testTag("admin_login_back_btn")
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Back",
                    tint = PureWhite
                )
            }
            Spacer(modifier = Modifier.width(12.dp))
            Column {
                Text(
                    text = "PRIVATE STUDIO SECURITY",
                    style = MaterialTheme.typography.labelSmall,
                    color = ElectricBlueLight
                )
                Text(
                    text = "Owner Admin Login",
                    style = MaterialTheme.typography.titleLarge,
                    color = PureWhite
                )
            }
        }

        Spacer(modifier = Modifier.height(36.dp))

        Surface(
            color = DarkNavySurface,
            shape = RoundedCornerShape(26.dp),
            modifier = Modifier
                .fillMaxWidth()
                .border(1.5.dp, PurpleGlassBorder, RoundedCornerShape(26.dp))
        ) {
            Column(
                modifier = Modifier
                    .background(VishnuCardGlassBrush)
                    .padding(26.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                VishnuBrandLogo(
                    showSubtitle = true,
                    logoSize = 72.dp
                )

                Spacer(modifier = Modifier.height(18.dp))

                Surface(
                    color = ElectricBlue.copy(alpha = 0.16f),
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.Lock,
                            contentDescription = null,
                            tint = ElectricBlueLight,
                            modifier = Modifier.size(15.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "SERVER-SIDE PASSWORD AUTHORIZATION",
                            style = MaterialTheme.typography.labelSmall,
                            color = ElectricBlueLight
                        )
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                Text(
                    text = "Restricted to the authorized VISHNU EDIT AI Owner. Enter your private Admin password to unlock content upload, editing, publishing, and deletion controls.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = SlateTextSecondary,
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(22.dp))

                OutlinedTextField(
                    value = password,
                    onValueChange = {
                        password = it
                        if (errorMessage != null) onClearError()
                    },
                    label = { Text("Owner Admin Password") },
                    placeholder = { Text("Enter Owner Password") },
                    singleLine = true,
                    visualTransformation = if (showPassword) {
                        VisualTransformation.None
                    } else {
                        PasswordVisualTransformation()
                    },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Default.Lock,
                            contentDescription = null,
                            tint = ElectricBlueLight
                        )
                    },
                    trailingIcon = {
                        IconButton(onClick = { showPassword = !showPassword }) {
                            Icon(
                                imageVector = if (showPassword) {
                                    Icons.Default.VisibilityOff
                                } else {
                                    Icons.Default.Visibility
                                },
                                contentDescription = if (showPassword) "Hide password" else "Show password",
                                tint = SlateTextSecondary
                            )
                        }
                    },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedContainerColor = DeepBlackBg,
                        unfocusedContainerColor = DeepBlackBg,
                        focusedBorderColor = ElectricBlue,
                        unfocusedBorderColor = GlassBorder,
                        focusedTextColor = PureWhite,
                        unfocusedTextColor = PureWhite
                    ),
                    shape = RoundedCornerShape(14.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("admin_password_input")
                )

                if (!errorMessage.isNullOrBlank()) {
                    Spacer(modifier = Modifier.height(12.dp))
                    Surface(
                        color = CrimsonDanger.copy(alpha = 0.16f),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .border(1.dp, CrimsonDanger, RoundedCornerShape(12.dp))
                    ) {
                        Text(
                            text = errorMessage,
                            style = MaterialTheme.typography.bodySmall,
                            color = CrimsonDanger,
                            textAlign = TextAlign.Center,
                            modifier = Modifier
                                .padding(12.dp)
                                .testTag("admin_login_error_text")
                        )
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))

                GradientActionButton(
                    text = if (isSubmitting) "Verifying on Server…" else "Unlock Owner Admin Dashboard",
                    icon = Icons.Default.Lock,
                    enabled = !isSubmitting && password.isNotBlank(),
                    onClick = { onSubmitPassword(password) },
                    modifier = Modifier.fillMaxWidth(),
                    testTag = "admin_password_submit_btn"
                )
            }
        }
    }
}

/**
 * OWNER ADMIN PANEL:
 * Private studio dashboard accessible only to the authenticated Owner.
 * - Upload new videos & thumbnails/photos
 * - Add/edit video title, description, AI prompt, category, model, aspect ratio, duration
 * - Publish / Unpublish toggle
 * - Separate EDIT and DELETE buttons on every video
 * - Delete confirmation dialog before deletion
 */
@Composable
fun OwnerAdminScreen(
    adminVideosState: UiState<List<VideoShowcase>>,
    editingVideo: VideoShowcase?,
    isUploadSheetOpen: Boolean,
    isSubmitting: Boolean,
    onBack: () -> Unit,
    onLogoutAdmin: () -> Unit,
    onStartCreateNew: () -> Unit,
    onStartEditVideo: (VideoShowcase) -> Unit,
    onDismissForm: () -> Unit,
    onSaveVideoForm: (
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
    ) -> Unit,
    onTogglePublish: (VideoShowcase) -> Unit,
    onDeleteVideo: (VideoShowcase) -> Unit,
    onSeedShowcase: () -> Unit,
    onPreviewVideo: (VideoShowcase) -> Unit
) {
    BackHandler {
        if (isUploadSheetOpen) {
            onDismissForm()
        } else {
            onBack()
        }
    }

    var selectedFilter by remember { mutableStateOf(AdminContentFilter.ALL) }
    var pendingDeleteVideo by remember { mutableStateOf<VideoShowcase?>(null) }

    // Delete Confirmation Dialog (Required by prompt: "Before deleting a video, show a confirmation message.")
    if (pendingDeleteVideo != null) {
        val target = pendingDeleteVideo!!
        AlertDialog(
            onDismissRequest = { pendingDeleteVideo = null },
            containerColor = DarkNavySurface,
            titleContentColor = PureWhite,
            textContentColor = PureWhite,
            title = {
                Text(
                    text = "Delete Video Permanently?",
                    style = MaterialTheme.typography.titleLarge
                )
            },
            text = {
                Text(
                    text = "Are you sure you want to permanently delete \"${target.title}\" and its AI generation prompt from VISHNU EDIT AI? This action cannot be undone.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = SlateTextSecondary
                )
            },
            confirmButton = {
                OutlinedButton(
                    onClick = {
                        onDeleteVideo(target)
                        pendingDeleteVideo = null
                    },
                    colors = ButtonDefaults.outlinedButtonColors(
                        containerColor = CrimsonDanger,
                        contentColor = PureWhite
                    ),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.testTag("confirm_delete_video_btn")
                ) {
                    Icon(
                        imageVector = Icons.Default.Delete,
                        contentDescription = null,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Confirm Delete")
                }
            },
            dismissButton = {
                TextButton(
                    onClick = { pendingDeleteVideo = null },
                    modifier = Modifier.testTag("cancel_delete_video_btn")
                ) {
                    Text("Cancel", color = SlateTextSecondary)
                }
            }
        )
    }

    // Upload / Edit Modal Dialog with all existing information pre-filled
    if (isUploadSheetOpen) {
        AdminUploadEditDialog(
            existingVideo = editingVideo,
            isSubmitting = isSubmitting,
            onDismiss = onDismissForm,
            onSave = onSaveVideoForm
        )
    }

    val allAdminVideos = (adminVideosState as? UiState.Success)?.data ?: emptyList()
    val publishedCount = allAdminVideos.count { it.published }
    val draftCount = allAdminVideos.count { !it.published }
    val filteredVideos = remember(allAdminVideos, selectedFilter) {
        when (selectedFilter) {
            AdminContentFilter.ALL -> allAdminVideos
            AdminContentFilter.PUBLISHED -> allAdminVideos.filter { it.published }
            AdminContentFilter.DRAFTS -> allAdminVideos.filter { !it.published }
        }
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(DeepBlackBg)
            .testTag("owner_admin_dashboard"),
        contentPadding = PaddingValues(bottom = 104.dp)
    ) {
        // Header Bar
        item {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(VishnuCardGlassBrush)
                    .padding(horizontal = 20.dp, vertical = 18.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.weight(1f)
                    ) {
                        IconButton(
                            onClick = onBack,
                            modifier = Modifier
                                .size(42.dp)
                                .clip(CircleShape)
                                .background(DarkNavyElevated)
                                .testTag("admin_back_button")
                        ) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                contentDescription = "Back",
                                tint = PureWhite
                            )
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text(
                                text = "PRIVATE OWNER CONTROL PANEL",
                                style = MaterialTheme.typography.labelSmall,
                                color = EmeraldSuccess
                            )
                            Text(
                                text = "VISHNU EDIT AI Admin Studio",
                                style = MaterialTheme.typography.titleLarge,
                                color = PureWhite
                            )
                        }
                    }

                    OutlinedButton(
                        onClick = onLogoutAdmin,
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.outlinedButtonColors(
                            contentColor = CrimsonDanger
                        ),
                        modifier = Modifier.testTag("admin_logout_lock_btn")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Lock,
                            contentDescription = "Lock Admin",
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Lock", style = MaterialTheme.typography.labelMedium)
                    }
                }

                Spacer(modifier = Modifier.height(18.dp))

                // Studio Metrics Row
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    AdminStatCard(
                        title = "Total Videos",
                        value = "${allAdminVideos.size}",
                        accent = ElectricBlueLight,
                        modifier = Modifier.weight(1f)
                    )
                    AdminStatCard(
                        title = "Published",
                        value = "$publishedCount",
                        accent = EmeraldSuccess,
                        modifier = Modifier.weight(1f)
                    )
                    AdminStatCard(
                        title = "Drafts",
                        value = "$draftCount",
                        accent = AmberWarning,
                        modifier = Modifier.weight(1f)
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    GradientActionButton(
                        text = "Upload New Video",
                        icon = Icons.Default.CloudUpload,
                        onClick = onStartCreateNew,
                        modifier = Modifier.weight(1f),
                        testTag = "admin_upload_new_video_btn"
                    )

                    OutlinedButton(
                        onClick = onSeedShowcase,
                        enabled = !isSubmitting,
                        shape = RoundedCornerShape(14.dp),
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = ElectricBlueLight),
                        modifier = Modifier
                            .height(48.dp)
                            .testTag("admin_sync_presets_btn")
                    ) {
                        Icon(
                            imageVector = Icons.Default.AutoAwesome,
                            contentDescription = null,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Restore 6 Presets")
                    }
                }
            }
        }

        // Filter Chips
        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 14.dp)
                    .horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                AdminContentFilter.entries.forEach { filter ->
                    FilterChip(
                        selected = selectedFilter == filter,
                        onClick = { selectedFilter = filter },
                        label = { Text(filter.label) },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = ElectricBlue,
                            selectedLabelColor = PureWhite,
                            containerColor = DarkNavySurface,
                            labelColor = SlateTextSecondary
                        )
                    )
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
                        .padding(horizontal = 20.dp, vertical = 16.dp)
                        .border(1.dp, GlassBorder, RoundedCornerShape(18.dp))
                ) {
                    Column(
                        modifier = Modifier.padding(28.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Icon(
                            imageVector = Icons.Default.CloudUpload,
                            contentDescription = null,
                            tint = ElectricBlueLight,
                            modifier = Modifier.size(40.dp)
                        )
                        Spacer(modifier = Modifier.height(10.dp))
                        Text(
                            text = "No videos in this view",
                            style = MaterialTheme.typography.titleMedium,
                            color = PureWhite
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = "Tap 'Upload New Video' or 'Restore 6 Presets' above to publish AI videos to the showcase.",
                            style = MaterialTheme.typography.bodySmall,
                            color = SlateTextSecondary
                        )
                    }
                }
            }
        } else {
            items(filteredVideos, key = { "admin_item_${it.id}" }) { video ->
                AdminVideoManagementCard(
                    video = video,
                    onEditClick = { onStartEditVideo(video) },
                    onDeleteClick = { pendingDeleteVideo = video },
                    onTogglePublish = { onTogglePublish(video) },
                    onPreviewClick = { onPreviewVideo(video) },
                    modifier = Modifier.padding(horizontal = 20.dp, vertical = 8.dp)
                )
            }
        }
    }
}

@Composable
private fun AdminStatCard(
    title: String,
    value: String,
    accent: androidx.compose.ui.graphics.Color,
    modifier: Modifier = Modifier
) {
    Surface(
        color = DarkNavySurface,
        shape = RoundedCornerShape(14.dp),
        modifier = modifier.border(1.dp, GlassBorder, RoundedCornerShape(14.dp))
    ) {
        Column(
            modifier = Modifier.padding(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = value,
                style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold),
                color = accent
            )
            Text(
                text = title,
                style = MaterialTheme.typography.labelSmall,
                color = SlateTextSecondary
            )
        }
    }
}

/**
 * Every video inside the private admin dashboard has separate EDIT and DELETE buttons,
 * plus a Publish / Unpublish toggle.
 */
@Composable
private fun AdminVideoManagementCard(
    video: VideoShowcase,
    onEditClick: () -> Unit,
    onDeleteClick: () -> Unit,
    onTogglePublish: () -> Unit,
    onPreviewClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        color = DarkNavySurface,
        shape = RoundedCornerShape(18.dp),
        modifier = modifier
            .fillMaxWidth()
            .border(
                width = 1.dp,
                color = if (video.published) GlassBorder else AmberWarning.copy(alpha = 0.5f),
                shape = RoundedCornerShape(18.dp)
            )
            .testTag("admin_video_card_${video.id}")
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onPreviewClick() },
                verticalAlignment = Alignment.CenterVertically
            ) {
                AsyncImage(
                    model = video.thumbnailUrl,
                    contentDescription = video.title,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .width(110.dp)
                        .height(66.dp)
                        .clip(RoundedCornerShape(10.dp))
                        .background(DarkNavyElevated)
                )
                Spacer(modifier = Modifier.width(12.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Surface(
                            color = if (video.published) EmeraldSuccess.copy(alpha = 0.2f) else AmberWarning.copy(alpha = 0.2f),
                            shape = RoundedCornerShape(6.dp)
                        ) {
                            Text(
                                text = if (video.published) "PUBLISHED" else "UNPUBLISHED",
                                style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp),
                                color = if (video.published) EmeraldSuccess else AmberWarning,
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                            )
                        }
                        Text(
                            text = video.category,
                            style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp),
                            color = ElectricBlueLight,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = video.title,
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                        color = PureWhite,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    Text(
                        text = "${video.aiModel} • ${video.aspectRatio} • ${video.formattedDuration}",
                        style = MaterialTheme.typography.bodySmall,
                        color = SlateTextSecondary
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Prompt snippet
            Surface(
                color = DeepBlackBg,
                shape = RoundedCornerShape(10.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = video.prompt,
                    style = MaterialTheme.typography.bodySmall.copy(
                        fontFamily = JetBrainsMonoFontFamily,
                        fontSize = 11.sp
                    ),
                    color = PureWhite.copy(alpha = 0.85f),
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.padding(10.dp)
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Separate EDIT, PUBLISH/UNPUBLISH, and DELETE buttons
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                OutlinedButton(
                    onClick = onEditClick,
                    shape = RoundedCornerShape(10.dp),
                    colors = ButtonDefaults.outlinedButtonColors(
                        containerColor = ElectricBlue.copy(alpha = 0.16f),
                        contentColor = ElectricBlueLight
                    ),
                    contentPadding = PaddingValues(horizontal = 12.dp, vertical = 8.dp),
                    modifier = Modifier
                        .weight(1f)
                        .height(42.dp)
                        .testTag("admin_edit_btn_${video.id}")
                ) {
                    Icon(
                        imageVector = Icons.Default.Edit,
                        contentDescription = "Edit Video",
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("EDIT", style = MaterialTheme.typography.labelLarge)
                }

                OutlinedButton(
                    onClick = onTogglePublish,
                    shape = RoundedCornerShape(10.dp),
                    colors = ButtonDefaults.outlinedButtonColors(
                        containerColor = DarkNavyElevated,
                        contentColor = if (video.published) AmberWarning else EmeraldSuccess
                    ),
                    contentPadding = PaddingValues(horizontal = 10.dp, vertical = 8.dp),
                    modifier = Modifier
                        .weight(1.1f)
                        .height(42.dp)
                        .testTag("admin_publish_toggle_${video.id}")
                ) {
                    Icon(
                        imageVector = if (video.published) Icons.Default.VisibilityOff else Icons.Default.Public,
                        contentDescription = null,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(5.dp))
                    Text(
                        text = if (video.published) "UNPUBLISH" else "PUBLISH",
                        style = MaterialTheme.typography.labelSmall
                    )
                }

                OutlinedButton(
                    onClick = onDeleteClick,
                    shape = RoundedCornerShape(10.dp),
                    colors = ButtonDefaults.outlinedButtonColors(
                        containerColor = CrimsonDanger.copy(alpha = 0.16f),
                        contentColor = CrimsonDanger
                    ),
                    contentPadding = PaddingValues(horizontal = 12.dp, vertical = 8.dp),
                    modifier = Modifier
                        .weight(1f)
                        .height(42.dp)
                        .testTag("admin_delete_btn_${video.id}")
                ) {
                    Icon(
                        imageVector = Icons.Default.Delete,
                        contentDescription = "Delete Video",
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("DELETE", style = MaterialTheme.typography.labelLarge)
                }
            }
        }
    }
}

/**
 * Full-screen Upload / Edit Modal Form with all existing information pre-filled when editing,
 * Android Photo Picker for selecting local video/image files, and Studio Cloud Presets.
 */
@Composable
private fun AdminUploadEditDialog(
    existingVideo: VideoShowcase?,
    isSubmitting: Boolean,
    onDismiss: () -> Unit,
    onSave: (
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
    ) -> Unit
) {
    val context = LocalContext.current
    val isEditing = existingVideo != null

    var title by remember(existingVideo) {
        mutableStateOf(existingVideo?.title ?: "")
    }
    var description by remember(existingVideo) {
        mutableStateOf(existingVideo?.description ?: "")
    }
    var prompt by remember(existingVideo) {
        mutableStateOf(existingVideo?.prompt ?: "")
    }
    var category by remember(existingVideo) {
        mutableStateOf(existingVideo?.category ?: VideoCategory.CINEMATIC.displayName)
    }
    var videoUrl by remember(existingVideo) {
        mutableStateOf(
            existingVideo?.videoUrl
                ?: "https://commondatastorage.googleapis.com/gtv-videos-bucket/sample/ForBiggerBlazes.mp4"
        )
    }
    var thumbnailUrl by remember(existingVideo) {
        mutableStateOf(
            existingVideo?.thumbnailUrl
                ?: "https://images.unsplash.com/photo-1518709268805-4e9042af9f23?auto=format&fit=crop&w=1200&q=85"
        )
    }
    var aiModel by remember(existingVideo) {
        mutableStateOf(existingVideo?.aiModel ?: "Veo 2 Cinema")
    }
    var aspectRatio by remember(existingVideo) {
        mutableStateOf(existingVideo?.aspectRatio ?: "16:9")
    }
    var durationSecondsText by remember(existingVideo) {
        mutableStateOf((existingVideo?.durationSeconds ?: 15L).toString())
    }
    var published by remember(existingVideo) {
        mutableStateOf(existingVideo?.published ?: true)
    }
    var featured by remember(existingVideo) {
        mutableStateOf(existingVideo?.featured ?: true)
    }
    var popularPrompt by remember(existingVideo) {
        mutableStateOf(existingVideo?.popularPrompt ?: true)
    }
    var validationError by remember { mutableStateOf<String?>(null) }

    // Zero-permission Android Photo Picker for Video selection
    val videoPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia()
    ) { uri ->
        if (uri != null) {
            try {
                context.contentResolver.takePersistableUriPermission(
                    uri,
                    Intent.FLAG_GRANT_READ_URI_PERMISSION
                )
            } catch (_: Exception) {
            }
            videoUrl = uri.toString()
        }
    }

    // Zero-permission Android Photo Picker for Thumbnail Image selection
    val thumbnailPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia()
    ) { uri ->
        if (uri != null) {
            try {
                context.contentResolver.takePersistableUriPermission(
                    uri,
                    Intent.FLAG_GRANT_READ_URI_PERMISSION
                )
            } catch (_: Exception) {
            }
            thumbnailUrl = uri.toString()
        }
    }

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Surface(
            color = DeepBlackBg,
            modifier = Modifier
                .fillMaxSize()
                .testTag("admin_upload_edit_dialog")
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 20.dp, vertical = 20.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = if (isEditing) "EDIT EXISTING VIDEO" else "UPLOAD NEW AI VIDEO",
                            style = MaterialTheme.typography.labelSmall,
                            color = ElectricBlueLight
                        )
                        Text(
                            text = if (isEditing) "Modify Video & Prompt" else "Publish to VISHNU EDIT AI",
                            style = MaterialTheme.typography.headlineSmall,
                            color = PureWhite
                        )
                    }
                    TextButton(onClick = onDismiss) {
                        Text("Close", color = SlateTextSecondary)
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Quick Fill Studio Cloud Presets bar
                Text(
                    text = "QUICK FILL STUDIO CLOUD MEDIA PRESET (OPTIONAL)",
                    style = MaterialTheme.typography.labelSmall,
                    color = NeonPurpleLight
                )
                Spacer(modifier = Modifier.height(6.dp))
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    StudioMediaCatalog.presets.forEach { preset ->
                        FilterChip(
                            selected = videoUrl == preset.videoUrl,
                            onClick = {
                                videoUrl = preset.videoUrl
                                thumbnailUrl = preset.thumbnailUrl
                                if (title.isBlank()) title = preset.defaultTitle
                                if (description.isBlank()) description = preset.defaultDescription
                                if (prompt.isBlank()) prompt = preset.defaultPrompt
                                category = preset.category.displayName
                                aiModel = preset.aiModel
                                aspectRatio = preset.aspectRatio
                                durationSecondsText = preset.durationSeconds.toString()
                            },
                            label = { Text(preset.category.badgeLabel) },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = NeonPurple,
                                selectedLabelColor = PureWhite,
                                containerColor = DarkNavySurface,
                                labelColor = SlateTextSecondary
                            )
                        )
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // 1. Video Title
                OutlinedTextField(
                    value = title,
                    onValueChange = { title = it },
                    label = { Text("Video Title *") },
                    singleLine = true,
                    colors = adminFieldColors(),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("form_video_title_input")
                )

                Spacer(modifier = Modifier.height(12.dp))

                // 2. Category Selector (All 6 Categories)
                Text(
                    text = "Select Video Category *",
                    style = MaterialTheme.typography.labelMedium,
                    color = PureWhite
                )
                Spacer(modifier = Modifier.height(6.dp))
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    VideoCategory.entries.forEach { cat ->
                        FilterChip(
                            selected = category == cat.displayName,
                            onClick = { category = cat.displayName },
                            label = { Text(cat.displayName) },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = ElectricBlue,
                                selectedLabelColor = PureWhite,
                                containerColor = DarkNavySurface,
                                labelColor = SlateTextSecondary
                            ),
                            modifier = Modifier.testTag("form_category_${cat.name}")
                        )
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // 3. Complete AI Generation Prompt
                OutlinedTextField(
                    value = prompt,
                    onValueChange = { prompt = it },
                    label = { Text("Complete AI Generation Prompt *") },
                    minLines = 4,
                    maxLines = 8,
                    colors = adminFieldColors(),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("form_video_prompt_input")
                )

                Spacer(modifier = Modifier.height(12.dp))

                // 4. Video Description
                OutlinedTextField(
                    value = description,
                    onValueChange = { description = it },
                    label = { Text("Video Description *") },
                    minLines = 2,
                    maxLines = 5,
                    colors = adminFieldColors(),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("form_video_description_input")
                )

                Spacer(modifier = Modifier.height(12.dp))

                // 5. Video File / Cloud Stream URL + Device Picker
                OutlinedTextField(
                    value = videoUrl,
                    onValueChange = { videoUrl = it },
                    label = { Text("Video Cloud URL or Local URI *") },
                    singleLine = true,
                    colors = adminFieldColors(),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("form_video_url_input")
                )
                Spacer(modifier = Modifier.height(6.dp))
                OutlinedButton(
                    onClick = {
                        videoPickerLauncher.launch(
                            PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.VideoOnly)
                        )
                    },
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = ElectricBlueLight),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Icon(
                        imageVector = Icons.Default.Movie,
                        contentDescription = null,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Pick Video File from Device")
                }

                Spacer(modifier = Modifier.height(12.dp))

                // 6. Thumbnail Image URL + Device Picker
                OutlinedTextField(
                    value = thumbnailUrl,
                    onValueChange = { thumbnailUrl = it },
                    label = { Text("Thumbnail Image URL or Local URI *") },
                    singleLine = true,
                    colors = adminFieldColors(),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("form_thumbnail_url_input")
                )
                Spacer(modifier = Modifier.height(6.dp))
                OutlinedButton(
                    onClick = {
                        thumbnailPickerLauncher.launch(
                            PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                        )
                    },
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = NeonPurpleLight),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Icon(
                        imageVector = Icons.Default.Image,
                        contentDescription = null,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Pick Thumbnail Photo from Device")
                }

                Spacer(modifier = Modifier.height(12.dp))

                // 7. AI Model, Aspect Ratio, Duration
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    OutlinedTextField(
                        value = aiModel,
                        onValueChange = { aiModel = it },
                        label = { Text("AI Model") },
                        singleLine = true,
                        colors = adminFieldColors(),
                        modifier = Modifier.weight(1.2f)
                    )
                    OutlinedTextField(
                        value = aspectRatio,
                        onValueChange = { aspectRatio = it },
                        label = { Text("Ratio") },
                        singleLine = true,
                        colors = adminFieldColors(),
                        modifier = Modifier.weight(0.8f)
                    )
                    OutlinedTextField(
                        value = durationSecondsText,
                        onValueChange = { durationSecondsText = it.filter { ch -> ch.isDigit() } },
                        label = { Text("Sec") },
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        colors = adminFieldColors(),
                        modifier = Modifier.weight(0.7f)
                    )
                }

                Spacer(modifier = Modifier.height(14.dp))

                // 8. Publish Status, Featured, Popular Prompt Toggles
                Surface(
                    color = DarkNavySurface,
                    shape = RoundedCornerShape(16.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .border(1.dp, GlassBorder, RoundedCornerShape(16.dp))
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = "Publish Video Publicly",
                                    style = MaterialTheme.typography.titleSmall,
                                    color = PureWhite
                                )
                                Text(
                                    text = if (published) "Visible to all visitors" else "Saved as private draft",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = SlateTextSecondary
                                )
                            }
                            Switch(
                                checked = published,
                                onCheckedChange = { published = it },
                                colors = SwitchDefaults.colors(
                                    checkedThumbColor = PureWhite,
                                    checkedTrackColor = EmeraldSuccess
                                )
                            )
                        }

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Checkbox(
                                checked = featured,
                                onCheckedChange = { featured = it },
                                colors = CheckboxDefaults.colors(checkedColor = ElectricBlue)
                            )
                            Text(
                                text = "Featured Hero Video",
                                style = MaterialTheme.typography.bodySmall,
                                color = PureWhite
                            )
                            Spacer(modifier = Modifier.width(16.dp))
                            Checkbox(
                                checked = popularPrompt,
                                onCheckedChange = { popularPrompt = it },
                                colors = CheckboxDefaults.colors(checkedColor = NeonPurple)
                            )
                            Text(
                                text = "Popular AI Prompt",
                                style = MaterialTheme.typography.bodySmall,
                                color = PureWhite
                            )
                        }
                    }
                }

                if (validationError != null) {
                    Spacer(modifier = Modifier.height(10.dp))
                    Text(
                        text = validationError!!,
                        style = MaterialTheme.typography.bodySmall,
                        color = CrimsonDanger
                    )
                }

                Spacer(modifier = Modifier.height(20.dp))

                if (isSubmitting) {
                    Box(
                        modifier = Modifier.fillMaxWidth(),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator(color = ElectricBlue)
                    }
                } else {
                    GradientActionButton(
                        text = if (isEditing) "Save Video Changes" else "Publish Video & Prompt",
                        icon = if (isEditing) Icons.Default.Edit else Icons.Default.AddCircle,
                        onClick = {
                            if (title.isBlank() || description.isBlank() || prompt.isBlank() ||
                                videoUrl.isBlank() || thumbnailUrl.isBlank()
                            ) {
                                validationError = "Please fill in Title, Description, Full AI Prompt, Video URL, and Thumbnail URL."
                            } else {
                                validationError = null
                                val dur = durationSecondsText.toLongOrNull()?.coerceIn(1L, 36000L) ?: 15L
                                onSave(
                                    title,
                                    description,
                                    videoUrl,
                                    thumbnailUrl,
                                    prompt,
                                    category,
                                    published,
                                    featured,
                                    popularPrompt,
                                    dur,
                                    aspectRatio,
                                    aiModel
                                )
                            }
                        },
                        modifier = Modifier.fillMaxWidth(),
                        testTag = "form_submit_save_btn"
                    )
                }

                Spacer(modifier = Modifier.height(40.dp))
            }
        }
    }
}

@Composable
private fun adminFieldColors() = OutlinedTextFieldDefaults.colors(
    focusedContainerColor = DarkNavySurface,
    unfocusedContainerColor = DarkNavySurface,
    focusedBorderColor = ElectricBlue,
    unfocusedBorderColor = GlassBorder,
    focusedTextColor = PureWhite,
    unfocusedTextColor = PureWhite,
    focusedLabelColor = ElectricBlueLight,
    unfocusedLabelColor = SlateTextSecondary
)
