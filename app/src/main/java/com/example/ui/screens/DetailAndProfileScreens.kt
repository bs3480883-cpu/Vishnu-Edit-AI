package com.example.ui.screens

import android.content.Intent
import android.net.Uri
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.AdminPanelSettings
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.BookmarkBorder
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Image
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.VerifiedUser
import androidx.compose.material.icons.filled.ZoomOutMap
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
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
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import coil.compose.AsyncImage
import com.example.data.model.SavedPrompt
import com.example.data.model.VideoShowcase
import com.example.ui.components.CinematicVideoPlayer
import com.example.ui.components.GradientActionButton
import com.example.ui.components.VishnuBrandLogo
import com.example.ui.components.copyPromptToClipboard
import com.example.ui.components.shareVideoAndPrompt
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
import com.example.ui.theme.VishnuPrimaryGradient
import java.text.SimpleDateFormat
import java.util.Locale

/**
 * VIDEO DETAILS SCREEN:
 * - Video thumbnail & real video playback
 * - Uploaded photo / high-res still frame viewer
 * - Video title
 * - Video description
 * - Category & AI Model metadata
 * - Full AI generation prompt
 * - Copy Prompt button ("Prompt Copied Successfully")
 * - Save Prompt & Share buttons
 */
@Composable
fun VideoDetailScreen(
    video: VideoShowcase,
    isSaved: Boolean,
    isAdmin: Boolean,
    onBack: () -> Unit,
    onCopyPrompt: () -> Unit,
    onToggleSave: () -> Unit,
    onEditVideo: (VideoShowcase) -> Unit
) {
    BackHandler { onBack() }
    val context = LocalContext.current
    var showFullPhotoPreview by remember { mutableStateOf(false) }
    val dateFormat = remember { SimpleDateFormat("MMM dd, yyyy", Locale.US) }
    val dateString = remember(video.createdAt) {
        video.createdAt?.toDate()?.let { dateFormat.format(it) } ?: "Studio Release"
    }

    if (showFullPhotoPreview) {
        Dialog(
            onDismissRequest = { showFullPhotoPreview = false },
            properties = DialogProperties(usePlatformDefaultWidth = false)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(DeepBlackBg.copy(alpha = 0.96f))
                    .padding(20.dp),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = "UPLOADED REFERENCE PHOTO",
                                style = MaterialTheme.typography.labelSmall,
                                color = ElectricBlueLight
                            )
                            Text(
                                text = video.title,
                                style = MaterialTheme.typography.titleMedium,
                                color = PureWhite
                            )
                        }
                        IconButton(
                            onClick = { showFullPhotoPreview = false },
                            modifier = Modifier
                                .size(42.dp)
                                .clip(CircleShape)
                                .background(DarkNavyElevated)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Close,
                                contentDescription = "Close Photo Preview",
                                tint = PureWhite
                            )
                        }
                    }
                    Spacer(modifier = Modifier.height(16.dp))
                    AsyncImage(
                        model = video.thumbnailUrl,
                        contentDescription = video.title,
                        contentScale = ContentScale.Fit,
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(20.dp))
                            .border(1.5.dp, ElectricBlue, RoundedCornerShape(20.dp))
                    )
                }
            }
        }
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(DeepBlackBg)
            .testTag("video_detail_screen"),
        contentPadding = PaddingValues(bottom = 96.dp)
    ) {
        // Top Bar
        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 14.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    IconButton(
                        onClick = onBack,
                        modifier = Modifier
                            .size(44.dp)
                            .clip(CircleShape)
                            .background(DarkNavySurface)
                            .testTag("video_detail_back_btn")
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
                            text = "VIDEO & PROMPT STUDIO",
                            style = MaterialTheme.typography.labelSmall,
                            color = ElectricBlueLight
                        )
                        Text(
                            text = video.category,
                            style = MaterialTheme.typography.titleMedium,
                            color = PureWhite
                        )
                    }
                }

                if (isAdmin) {
                    OutlinedButton(
                        onClick = { onEditVideo(video) },
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = ElectricBlueLight),
                        modifier = Modifier.testTag("detail_admin_edit_btn")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Edit,
                            contentDescription = "Edit Video",
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Edit", style = MaterialTheme.typography.labelLarge)
                    }
                }
            }
        }

        // Video Player
        item {
            CinematicVideoPlayer(
                videoUrl = video.videoUrl,
                thumbnailUrl = video.thumbnailUrl,
                title = video.title,
                modifier = Modifier.padding(horizontal = 20.dp)
            )
            Spacer(modifier = Modifier.height(18.dp))
        }

        // Video Title, Metadata & Description
        item {
            Column(modifier = Modifier.padding(horizontal = 20.dp)) {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Surface(
                        color = ElectricBlue.copy(alpha = 0.22f),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text(
                            text = video.category,
                            style = MaterialTheme.typography.labelSmall,
                            color = ElectricBlueLight,
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp)
                        )
                    }
                    Surface(
                        color = NeonPurple.copy(alpha = 0.22f),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text(
                            text = video.aiModel,
                            style = MaterialTheme.typography.labelSmall,
                            color = NeonPurpleLight,
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp)
                        )
                    }
                    Surface(
                        color = DarkNavyElevated,
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text(
                            text = "${video.aspectRatio} • ${video.formattedDuration}",
                            style = MaterialTheme.typography.labelSmall,
                            color = SlateTextSecondary,
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))
                Text(
                    text = video.title,
                    style = MaterialTheme.typography.headlineMedium,
                    color = PureWhite
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Published $dateString • VISHNU EDIT AI Official Showcase",
                    style = MaterialTheme.typography.labelSmall,
                    color = SlateTextSecondary
                )

                Spacer(modifier = Modifier.height(14.dp))
                Text(
                    text = video.description,
                    style = MaterialTheme.typography.bodyLarge,
                    color = PureWhite.copy(alpha = 0.88f)
                )
            }
            Spacer(modifier = Modifier.height(22.dp))
        }

        // Full AI Generation Prompt Card + Copy Prompt + Share
        item {
            Surface(
                color = DarkNavySurface,
                shape = RoundedCornerShape(22.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp)
                    .border(1.5.dp, PurpleGlassBorder, RoundedCornerShape(22.dp))
            ) {
                Column(
                    modifier = Modifier
                        .background(VishnuCardGlassBrush)
                        .padding(20.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.AutoAwesome,
                                contentDescription = null,
                                tint = NeonPurpleLight,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "COMPLETE AI GENERATION PROMPT",
                                style = MaterialTheme.typography.labelLarge,
                                color = PureWhite
                            )
                        }

                        Text(
                            text = "${video.prompt.length} chars",
                            style = MaterialTheme.typography.labelSmall,
                            color = SlateTextSecondary
                        )
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    Surface(
                        color = DeepBlackBg,
                        shape = RoundedCornerShape(14.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .border(1.dp, GlassBorder, RoundedCornerShape(14.dp))
                    ) {
                        Text(
                            text = video.prompt,
                            style = MaterialTheme.typography.bodyMedium.copy(
                                fontFamily = JetBrainsMonoFontFamily,
                                lineHeight = 22.sp
                            ),
                            color = PureWhite,
                            modifier = Modifier
                                .padding(16.dp)
                                .testTag("detail_full_prompt_text")
                        )
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    GradientActionButton(
                        text = "COPY PROMPT",
                        icon = Icons.Default.ContentCopy,
                        onClick = {
                            copyPromptToClipboard(context, video.prompt, onCopyPrompt)
                        },
                        modifier = Modifier.fillMaxWidth(),
                        testTag = "detail_copy_prompt_btn"
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        OutlinedButton(
                            onClick = onToggleSave,
                            shape = RoundedCornerShape(14.dp),
                            colors = ButtonDefaults.outlinedButtonColors(
                                containerColor = DarkNavyElevated,
                                contentColor = PureWhite
                            ),
                            modifier = Modifier
                                .weight(1f)
                                .height(48.dp)
                                .testTag("detail_save_prompt_btn")
                        ) {
                            Icon(
                                imageVector = if (isSaved) Icons.Default.Bookmark else Icons.Default.BookmarkBorder,
                                contentDescription = null,
                                tint = if (isSaved) NeonPurpleLight else PureWhite,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = if (isSaved) "Saved" else "Save Prompt",
                                style = MaterialTheme.typography.labelLarge
                            )
                        }

                        OutlinedButton(
                            onClick = { shareVideoAndPrompt(context, video) },
                            shape = RoundedCornerShape(14.dp),
                            colors = ButtonDefaults.outlinedButtonColors(
                                containerColor = DarkNavyElevated,
                                contentColor = PureWhite
                            ),
                            modifier = Modifier
                                .weight(1f)
                                .height(48.dp)
                                .testTag("detail_share_btn")
                        ) {
                            Icon(
                                imageVector = Icons.Default.Share,
                                contentDescription = null,
                                tint = ElectricBlueLight,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Share Video",
                                style = MaterialTheme.typography.labelLarge
                            )
                        }
                    }
                }
            }
            Spacer(modifier = Modifier.height(20.dp))
        }

        // Uploaded Reference Photo / Thumbnail Card
        item {
            Surface(
                color = DarkNavySurface,
                shape = RoundedCornerShape(22.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp)
                    .border(1.dp, GlassBorder, RoundedCornerShape(22.dp))
            ) {
                Column(modifier = Modifier.padding(18.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.Image,
                                contentDescription = null,
                                tint = ElectricBlueLight,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "UPLOADED PHOTO & KEYFRAME",
                                style = MaterialTheme.typography.labelLarge,
                                color = PureWhite
                            )
                        }
                        TextButton(
                            onClick = { showFullPhotoPreview = true },
                            modifier = Modifier.testTag("detail_expand_photo_btn")
                        ) {
                            Icon(
                                imageVector = Icons.Default.ZoomOutMap,
                                contentDescription = null,
                                tint = ElectricBlueLight,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Full Photo", color = ElectricBlueLight)
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    AsyncImage(
                        model = video.thumbnailUrl,
                        contentDescription = "${video.title} Uploaded Photo",
                        contentScale = ContentScale.Crop,
                        modifier = Modifier
                            .fillMaxWidth()
                            .aspectRatio(16f / 9f)
                            .clip(RoundedCornerShape(16.dp))
                            .border(1.dp, GlassBorder, RoundedCornerShape(16.dp))
                            .clickable { showFullPhotoPreview = true }
                    )
                }
            }
        }
    }
}

/**
 * 5. PROFILE SCREEN:
 * - VISHNU EDIT AI branding
 * - App information
 * - Saved prompts summary
 * - About section
 * - Contact section
 * - Discreet Owner Admin Portal navigation (NO popup dialogs for public users)
 */
@Composable
fun ProfileScreen(
    isAdmin: Boolean,
    savedPrompts: List<SavedPrompt>,
    totalPublishedCount: Int,
    onOpenAdminPortal: () -> Unit,
    onLogoutAdmin: () -> Unit,
    onNavigateToSavedPrompts: () -> Unit
) {
    val context = LocalContext.current

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(DeepBlackBg)
            .testTag("profile_screen_list"),
        contentPadding = PaddingValues(horizontal = 20.dp, vertical = 20.dp)
    ) {
        // 1. VISHNU EDIT AI Branding Header
        item {
            Surface(
                color = DarkNavySurface,
                shape = RoundedCornerShape(24.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .border(1.dp, GlassBorder, RoundedCornerShape(24.dp))
            ) {
                Column(
                    modifier = Modifier
                        .background(VishnuCardGlassBrush)
                        .padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    VishnuBrandLogo(
                        showSubtitle = true,
                        logoSize = 72.dp
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    Surface(
                        color = if (isAdmin) EmeraldSuccess.copy(alpha = 0.18f) else ElectricBlue.copy(alpha = 0.18f),
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = if (isAdmin) Icons.Default.AdminPanelSettings else Icons.Default.VerifiedUser,
                                contentDescription = null,
                                tint = if (isAdmin) EmeraldSuccess else ElectricBlueLight,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = if (isAdmin) "AUTHORIZED STUDIO OWNER" else "PUBLIC SHOWCASE ACCESS",
                                style = MaterialTheme.typography.labelSmall,
                                color = if (isAdmin) EmeraldSuccess else ElectricBlueLight
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))
                    Text(
                        text = "AI Video Showcase & Prompt Library",
                        style = MaterialTheme.typography.titleMedium,
                        color = PureWhite
                    )
                    Text(
                        text = "Instant public access • No user login required",
                        style = MaterialTheme.typography.bodySmall,
                        color = SlateTextSecondary
                    )

                    Spacer(modifier = Modifier.height(18.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceEvenly
                    ) {
                        ProfileStatColumn(value = "$totalPublishedCount", label = "Live Videos")
                        ProfileStatColumn(value = "${savedPrompts.size}", label = "Saved Prompts")
                        ProfileStatColumn(value = "6", label = "AI Categories")
                    }
                }
            }
            Spacer(modifier = Modifier.height(18.dp))
        }

        // 2. Saved Prompts Quick Section
        item {
            Surface(
                color = DarkNavySurface,
                shape = RoundedCornerShape(20.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .border(1.dp, GlassBorder, RoundedCornerShape(20.dp))
            ) {
                Column(modifier = Modifier.padding(20.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Saved Prompts (${savedPrompts.size})",
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                            color = PureWhite
                        )
                        TextButton(onClick = onNavigateToSavedPrompts) {
                            Text("Open Library", color = ElectricBlueLight)
                        }
                    }
                    Text(
                        text = if (savedPrompts.isEmpty()) {
                            "Bookmark any AI generation prompt with one tap to access it anytime."
                        } else {
                            "Latest saved: ${savedPrompts.take(2).joinToString(" • ") { it.videoTitle }}"
                        },
                        style = MaterialTheme.typography.bodySmall,
                        color = SlateTextSecondary
                    )
                }
            }
            Spacer(modifier = Modifier.height(18.dp))
        }

        // 3. App Information & About Section
        item {
            Surface(
                color = DarkNavySurface,
                shape = RoundedCornerShape(20.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .border(1.dp, GlassBorder, RoundedCornerShape(20.dp))
            ) {
                Column(modifier = Modifier.padding(20.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Info,
                            contentDescription = null,
                            tint = ElectricBlueLight,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "About VISHNU EDIT AI",
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                            color = PureWhite
                        )
                    }
                    Spacer(modifier = Modifier.height(10.dp))
                    Text(
                        text = "VISHNU EDIT AI is a professional AI Video Showcase and Prompt Sharing platform. Public visitors can freely watch all published AI videos, view uploaded reference photos, browse 6 specialized categories, and copy complete AI generation prompts with a single click.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = SlateTextSecondary
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        text = "• App Version: 1.0.0 Production Studio\n• Access Mode: Open Public Showcase (No User Login Required)\n• Supported Prompt Engines: Veo 2, Sora Studio, Runway Gen-3, Kling Pro",
                        style = MaterialTheme.typography.bodySmall,
                        color = PureWhite.copy(alpha = 0.85f)
                    )
                }
            }
            Spacer(modifier = Modifier.height(18.dp))
        }

        // 4. Contact Section
        item {
            Surface(
                color = DarkNavySurface,
                shape = RoundedCornerShape(20.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .border(1.dp, GlassBorder, RoundedCornerShape(20.dp))
            ) {
                Column(modifier = Modifier.padding(20.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Email,
                            contentDescription = null,
                            tint = NeonPurpleLight,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Contact & Studio Inquiries",
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                            color = PureWhite
                        )
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "Studio Contact: bs3480883@gmail.com\nFor custom AI film commissions, commercial VFX licensing, or creative collaborations, contact VISHNU EDIT AI directly.",
                        style = MaterialTheme.typography.bodySmall,
                        color = SlateTextSecondary
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    OutlinedButton(
                        onClick = {
                            val emailIntent = Intent(Intent.ACTION_SENDTO).apply {
                                data = Uri.parse("mailto:bs3480883@gmail.com")
                                putExtra(Intent.EXTRA_SUBJECT, "VISHNU EDIT AI Studio Inquiry")
                                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                            }
                            try {
                                context.startActivity(emailIntent)
                            } catch (_: Exception) {
                            }
                        },
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = PureWhite),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Icon(
                            imageVector = Icons.Default.Email,
                            contentDescription = null,
                            tint = ElectricBlueLight,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Contact VISHNU EDIT AI Studio")
                    }
                }
            }
            Spacer(modifier = Modifier.height(18.dp))
        }

        // 5. Private Owner Admin Portal Entry (Opens dedicated screen, never a popup)
        item {
            Surface(
                color = DarkNavySurface,
                shape = RoundedCornerShape(20.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .border(1.dp, if (isAdmin) ElectricBlue else PurpleGlassBorder, RoundedCornerShape(20.dp))
            ) {
                Column(modifier = Modifier.padding(20.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(42.dp)
                                .clip(RoundedCornerShape(12.dp))
                                .background(VishnuPrimaryGradient),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.AdminPanelSettings,
                                contentDescription = null,
                                tint = PureWhite
                            )
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "Private Owner Admin Dashboard",
                                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                                color = PureWhite
                            )
                            Text(
                                text = if (isAdmin) {
                                    "Owner session active. Upload, edit, publish/unpublish, and delete content."
                                } else {
                                    "Restricted to the authorized VISHNU EDIT AI owner."
                                },
                                style = MaterialTheme.typography.bodySmall,
                                color = SlateTextSecondary
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    if (isAdmin) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            GradientActionButton(
                                text = "Open Admin Dashboard",
                                icon = Icons.Default.AdminPanelSettings,
                                onClick = onOpenAdminPortal,
                                modifier = Modifier.weight(1f),
                                testTag = "open_owner_admin_btn"
                            )
                            OutlinedButton(
                                onClick = onLogoutAdmin,
                                shape = RoundedCornerShape(14.dp),
                                colors = ButtonDefaults.outlinedButtonColors(
                                    contentColor = MaterialTheme.colorScheme.error
                                ),
                                modifier = Modifier
                                    .height(48.dp)
                                    .testTag("lock_admin_session_btn")
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Lock,
                                    contentDescription = "Lock Admin",
                                    modifier = Modifier.size(18.dp)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("Lock")
                            }
                        }
                    } else {
                        OutlinedButton(
                            onClick = onOpenAdminPortal,
                            shape = RoundedCornerShape(14.dp),
                            colors = ButtonDefaults.outlinedButtonColors(contentColor = ElectricBlueLight),
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(48.dp)
                                .testTag("owner_login_verify_btn")
                        ) {
                            Icon(
                                imageVector = Icons.Default.Lock,
                                contentDescription = null,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Owner Admin Login")
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(100.dp))
        }
    }
}

@Composable
private fun ProfileStatColumn(value: String, label: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            text = value,
            style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold),
            color = PureWhite
        )
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall,
            color = SlateTextSecondary
        )
    }
}
