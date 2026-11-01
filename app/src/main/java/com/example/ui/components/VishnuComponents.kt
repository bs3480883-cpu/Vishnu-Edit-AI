package com.example.ui.components

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.media.MediaPlayer
import android.net.Uri
import android.widget.VideoView
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.Canvas
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.VolumeOff
import androidx.compose.material.icons.automirrored.filled.VolumeUp
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.BookmarkBorder
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.MovieCreation
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Replay
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import coil.compose.AsyncImage
import com.example.data.model.VideoCategory
import com.example.data.model.VideoShowcase
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
import com.example.ui.theme.VishnuHeroOverlayBrush
import com.example.ui.theme.VishnuPrimaryGradient
import kotlinx.coroutines.delay

fun copyPromptToClipboard(
    context: Context,
    promptText: String,
    onCopied: () -> Unit
) {
    val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as? ClipboardManager
    clipboard?.setPrimaryClip(ClipData.newPlainText("VISHNU EDIT AI Prompt", promptText))
    onCopied()
}

fun shareVideoAndPrompt(context: Context, video: VideoShowcase) {
    val shareBody = buildString {
        appendLine("🎬 ${video.title} — VISHNU EDIT AI")
        appendLine("Category: ${video.category} | Model: ${video.aiModel}")
        appendLine()
        appendLine("✨ AI Generation Prompt:")
        appendLine(video.prompt)
        appendLine()
        appendLine("Watch Video: ${video.videoUrl}")
    }
    val sendIntent = Intent(Intent.ACTION_SEND).apply {
        type = "text/plain"
        putExtra(Intent.EXTRA_SUBJECT, "VISHNU EDIT AI: ${video.title}")
        putExtra(Intent.EXTRA_TEXT, shareBody)
    }
    val chooser = Intent.createChooser(sendIntent, "Share AI Video & Prompt").apply {
        addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
    }
    context.startActivity(chooser)
}

/**
 * Custom VISHNU EDIT AI Emblem:
 * A cinematic play button combined with AI neural circuit nodes and a creative editing frame
 * using electric blue (#287BFF) and purple (#9146FF) gradients, displaying VISHNU EDIT AI below.
 */
@Composable
fun VishnuBrandLogo(
    modifier: Modifier = Modifier,
    showSubtitle: Boolean = true,
    logoSize: Dp = 64.dp,
    compactHorizontal: Boolean = false
) {
    if (compactHorizontal) {
        Row(
            modifier = modifier,
            verticalAlignment = Alignment.CenterVertically
        ) {
            VishnuLogoCanvas(size = logoSize)
            Spacer(modifier = Modifier.width(12.dp))
            Column {
                Text(
                    text = "VISHNU EDIT AI",
                    style = MaterialTheme.typography.titleLarge.copy(
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.4.sp
                    ),
                    color = PureWhite
                )
                if (showSubtitle) {
                    Text(
                        text = "AI VIDEO & PROMPT STUDIO",
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontSize = 10.sp,
                            letterSpacing = 1.2.sp
                        ),
                        color = ElectricBlueLight
                    )
                }
            }
        }
    } else {
        Column(
            modifier = modifier,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            VishnuLogoCanvas(size = logoSize)
            Spacer(modifier = Modifier.height(10.dp))
            Text(
                text = "VISHNU EDIT AI",
                style = MaterialTheme.typography.headlineMedium.copy(
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 2.sp
                ),
                color = PureWhite,
                textAlign = TextAlign.Center
            )
            if (showSubtitle) {
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "CINEMATIC AI VIDEO SHOWCASE & PROMPT STUDIO",
                    style = MaterialTheme.typography.labelSmall.copy(
                        letterSpacing = 1.2.sp
                    ),
                    color = ElectricBlueLight,
                    textAlign = TextAlign.Center
                )
            }
        }
    }
}

@Composable
private fun VishnuLogoCanvas(size: Dp) {
    Box(
        modifier = Modifier
            .size(size)
            .clip(RoundedCornerShape(size * 0.28f))
            .background(
                Brush.linearGradient(
                    colors = listOf(
                        Color(0xFF0E172A),
                        Color(0xFF1A1438)
                    )
                )
            )
            .border(
                width = 1.5.dp,
                brush = Brush.linearGradient(listOf(ElectricBlue, NeonPurple)),
                shape = RoundedCornerShape(size * 0.28f)
            ),
        contentAlignment = Alignment.Center
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val w = this.size.width
            val h = this.size.height
            val gradientBrush = Brush.linearGradient(
                colors = listOf(ElectricBlue, NeonPurple),
                start = Offset(0f, 0f),
                end = Offset(w, h)
            )

            // Outer creative editing frame corners
            val inset = w * 0.16f
            val cornerLen = w * 0.14f
            val strokeW = w * 0.045f

            // Top-left editing bracket
            drawLine(
                brush = gradientBrush,
                start = Offset(inset, inset),
                end = Offset(inset + cornerLen, inset),
                strokeWidth = strokeW,
                cap = StrokeCap.Round
            )
            drawLine(
                brush = gradientBrush,
                start = Offset(inset, inset),
                end = Offset(inset, inset + cornerLen),
                strokeWidth = strokeW,
                cap = StrokeCap.Round
            )

            // Bottom-right editing bracket
            drawLine(
                brush = gradientBrush,
                start = Offset(w - inset, h - inset),
                end = Offset(w - inset - cornerLen, h - inset),
                strokeWidth = strokeW,
                cap = StrokeCap.Round
            )
            drawLine(
                brush = gradientBrush,
                start = Offset(w - inset, h - inset),
                end = Offset(w - inset, h - inset - cornerLen),
                strokeWidth = strokeW,
                cap = StrokeCap.Round
            )

            // Central Cinematic Play Triangle
            val playPath = Path().apply {
                moveTo(w * 0.36f, h * 0.27f)
                lineTo(w * 0.74f, h * 0.50f)
                lineTo(w * 0.36f, h * 0.73f)
                close()
            }
            drawPath(
                path = playPath,
                brush = gradientBrush
            )
            drawPath(
                path = playPath,
                color = PureWhite.copy(alpha = 0.35f),
                style = Stroke(width = w * 0.02f, join = StrokeJoin.Round)
            )

            // AI Neural Spark Star (Top Right)
            val sparkCx = w * 0.74f
            val sparkCy = h * 0.24f
            val sparkR = w * 0.09f
            val sparkPath = Path().apply {
                moveTo(sparkCx, sparkCy - sparkR)
                lineTo(sparkCx + sparkR * 0.3f, sparkCy - sparkR * 0.3f)
                lineTo(sparkCx + sparkR, sparkCy)
                lineTo(sparkCx + sparkR * 0.3f, sparkCy + sparkR * 0.3f)
                lineTo(sparkCx, sparkCy + sparkR)
                lineTo(sparkCx - sparkR * 0.3f, sparkCy + sparkR * 0.3f)
                lineTo(sparkCx - sparkR, sparkCy)
                lineTo(sparkCx - sparkR * 0.3f, sparkCy - sparkR * 0.3f)
                close()
            }
            drawPath(path = sparkPath, color = PureWhite)

            // Creative timeline nodes (Left side)
            drawCircle(
                color = ElectricBlueLight,
                radius = w * 0.035f,
                center = Offset(w * 0.24f, h * 0.42f)
            )
            drawCircle(
                color = NeonPurpleLight,
                radius = w * 0.035f,
                center = Offset(w * 0.24f, h * 0.58f)
            )
        }
    }
}

@Composable
fun GradientActionButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    icon: androidx.compose.ui.graphics.vector.ImageVector? = null,
    enabled: Boolean = true,
    testTag: String? = null
) {
    val finalModifier = if (testTag != null) modifier.testTag(testTag) else modifier
    Button(
        onClick = onClick,
        enabled = enabled,
        shape = RoundedCornerShape(14.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = Color.Transparent,
            disabledContainerColor = DarkNavyElevated
        ),
        contentPadding = PaddingValues(horizontal = 18.dp, vertical = 12.dp),
        modifier = finalModifier
            .height(48.dp)
            .background(
                brush = if (enabled) VishnuPrimaryGradient else Brush.horizontalGradient(
                    listOf(DarkNavyElevated, DarkNavyElevated)
                ),
                shape = RoundedCornerShape(14.dp)
            )
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            if (icon != null) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = PureWhite,
                    modifier = Modifier.size(18.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
            }
            Text(
                text = text,
                style = MaterialTheme.typography.labelLarge,
                color = PureWhite
            )
        }
    }
}

/**
 * Featured Hero Video Card for the Home Screen horizontal showcase.
 */
@Composable
fun FeaturedVideoCard(
    video: VideoShowcase,
    isSaved: Boolean,
    onVideoClick: () -> Unit,
    onCopyPrompt: () -> Unit,
    onToggleSave: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val cat = VideoCategory.fromDisplayName(video.category)

    Surface(
        modifier = modifier
            .width(320.dp)
            .clip(RoundedCornerShape(22.dp))
            .border(1.dp, GlassBorder, RoundedCornerShape(22.dp))
            .clickable { onVideoClick() }
            .testTag("featured_video_card_${video.id}"),
        color = DarkNavySurface,
        shape = RoundedCornerShape(22.dp)
    ) {
        Column {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(185.dp)
            ) {
                AsyncImage(
                    model = video.thumbnailUrl,
                    contentDescription = video.title,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize()
                )
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(VishnuHeroOverlayBrush)
                )

                // Top badges
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(12.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Surface(
                        color = ElectricBlue.copy(alpha = 0.88f),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text(
                            text = cat.badgeLabel,
                            style = MaterialTheme.typography.labelSmall,
                            color = PureWhite,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                        )
                    }

                    Surface(
                        color = DeepBlackBg.copy(alpha = 0.78f),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.border(0.5.dp, PurpleGlassBorder, RoundedCornerShape(8.dp))
                    ) {
                        Text(
                            text = "${video.aiModel} • ${video.formattedDuration}",
                            style = MaterialTheme.typography.labelSmall,
                            color = NeonPurpleLight,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                        )
                    }
                }

                // Center Play Orb
                Box(
                    modifier = Modifier
                        .size(52.dp)
                        .align(Alignment.Center)
                        .clip(CircleShape)
                        .background(VishnuPrimaryGradient)
                        .border(1.5.dp, PureWhite.copy(alpha = 0.6f), CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.PlayArrow,
                        contentDescription = "Play ${video.title}",
                        tint = PureWhite,
                        modifier = Modifier.size(28.dp)
                    )
                }

                // Title overlay bottom
                Column(
                    modifier = Modifier
                        .align(Alignment.BottomStart)
                        .padding(14.dp)
                ) {
                    Text(
                        text = video.title,
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                        color = PureWhite,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    Text(
                        text = video.description,
                        style = MaterialTheme.typography.bodySmall,
                        color = SlateTextSecondary,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }

            // Prompt Strip + Copy Prompt CTA
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(VishnuCardGlassBrush)
                    .padding(14.dp)
            ) {
                Text(
                    text = "AI GENERATION PROMPT",
                    style = MaterialTheme.typography.labelSmall,
                    color = ElectricBlueLight
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = video.prompt,
                    style = MaterialTheme.typography.bodySmall.copy(
                        fontFamily = JetBrainsMonoFontFamily,
                        fontSize = 11.5.sp
                    ),
                    color = PureWhite.copy(alpha = 0.9f),
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
                Spacer(modifier = Modifier.height(10.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    GradientActionButton(
                        text = "COPY PROMPT",
                        icon = Icons.Default.ContentCopy,
                        onClick = {
                            copyPromptToClipboard(context, video.prompt, onCopyPrompt)
                        },
                        modifier = Modifier.weight(1f),
                        testTag = "copy_prompt_featured_${video.id}"
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    IconButton(
                        onClick = onToggleSave,
                        modifier = Modifier
                            .size(48.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .background(DarkNavyElevated)
                    ) {
                        Icon(
                            imageVector = if (isSaved) Icons.Default.Bookmark else Icons.Default.BookmarkBorder,
                            contentDescription = "Save Prompt",
                            tint = if (isSaved) NeonPurpleLight else PureWhite
                        )
                    }
                }
            }
        }
    }
}

/**
 * Full-width AI Video & Prompt Card used in Latest Uploads, Categories, and Search.
 */
@Composable
fun VideoShowcaseCard(
    video: VideoShowcase,
    isSaved: Boolean,
    onVideoClick: () -> Unit,
    onCopyPrompt: () -> Unit,
    onToggleSave: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val cat = VideoCategory.fromDisplayName(video.category)

    Surface(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(20.dp))
            .border(1.dp, GlassBorder, RoundedCornerShape(20.dp))
            .clickable { onVideoClick() }
            .testTag("video_card_${video.id}"),
        color = DarkNavySurface,
        shape = RoundedCornerShape(20.dp)
    ) {
        Column {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(16f / 9f)
            ) {
                AsyncImage(
                    model = video.thumbnailUrl,
                    contentDescription = video.title,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize()
                )
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(VishnuHeroOverlayBrush)
                )

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(12.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Surface(
                        color = DeepBlackBg.copy(alpha = 0.82f),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.border(0.8.dp, ElectricBlue.copy(alpha = 0.6f), RoundedCornerShape(8.dp))
                    ) {
                        Text(
                            text = cat.displayName,
                            style = MaterialTheme.typography.labelSmall,
                            color = ElectricBlueLight,
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp)
                        )
                    }

                    Surface(
                        color = DeepBlackBg.copy(alpha = 0.82f),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text(
                            text = "${video.aspectRatio} • ${video.formattedDuration}",
                            style = MaterialTheme.typography.labelSmall,
                            color = PureWhite,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                        )
                    }
                }

                // Play button in center
                Box(
                    modifier = Modifier
                        .size(54.dp)
                        .align(Alignment.Center)
                        .clip(CircleShape)
                        .background(VishnuPrimaryGradient)
                        .border(1.5.dp, PureWhite.copy(alpha = 0.7f), CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.PlayArrow,
                        contentDescription = "Watch ${video.title}",
                        tint = PureWhite,
                        modifier = Modifier.size(30.dp)
                    )
                }

                // AI Model tag bottom right
                Surface(
                    color = NeonPurple.copy(alpha = 0.88f),
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier
                        .align(Alignment.BottomEnd)
                        .padding(12.dp)
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.AutoAwesome,
                            contentDescription = null,
                            tint = PureWhite,
                            modifier = Modifier.size(12.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = video.aiModel,
                            style = MaterialTheme.typography.labelSmall,
                            color = PureWhite
                        )
                    }
                }
            }

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(VishnuCardGlassBrush)
                    .padding(16.dp)
            ) {
                Text(
                    text = video.title,
                    style = MaterialTheme.typography.titleLarge,
                    color = PureWhite
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = video.description,
                    style = MaterialTheme.typography.bodyMedium,
                    color = SlateTextSecondary,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )

                Spacer(modifier = Modifier.height(12.dp))

                // Complete AI Prompt Display Box below video
                Surface(
                    color = DeepBlackBg.copy(alpha = 0.85f),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .border(1.dp, PurpleGlassBorder, RoundedCornerShape(12.dp))
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "FULL AI GENERATION PROMPT",
                                style = MaterialTheme.typography.labelSmall,
                                color = NeonPurpleLight
                            )
                            Text(
                                text = "READY TO PASTE",
                                style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp),
                                color = ElectricBlueLight
                            )
                        }
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = video.prompt,
                            style = MaterialTheme.typography.bodySmall.copy(
                                fontFamily = JetBrainsMonoFontFamily,
                                lineHeight = 18.sp
                            ),
                            color = PureWhite.copy(alpha = 0.92f)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    GradientActionButton(
                        text = "COPY PROMPT",
                        icon = Icons.Default.ContentCopy,
                        onClick = {
                            copyPromptToClipboard(context, video.prompt, onCopyPrompt)
                        },
                        modifier = Modifier.weight(1f),
                        testTag = "copy_prompt_btn_${video.id}"
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    IconButton(
                        onClick = onToggleSave,
                        modifier = Modifier
                            .size(48.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .background(DarkNavyElevated)
                            .testTag("save_prompt_btn_${video.id}")
                    ) {
                        Icon(
                            imageVector = if (isSaved) Icons.Default.Bookmark else Icons.Default.BookmarkBorder,
                            contentDescription = "Save Prompt",
                            tint = if (isSaved) NeonPurpleLight else PureWhite
                        )
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    IconButton(
                        onClick = { shareVideoAndPrompt(context, video) },
                        modifier = Modifier
                            .size(48.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .background(DarkNavyElevated)
                            .testTag("share_video_btn_${video.id}")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Share,
                            contentDescription = "Share Video",
                            tint = PureWhite
                        )
                    }
                }
            }
        }
    }
}

/**
 * Real Android VideoView / MediaPlayer Player with custom glassmorphism controls.
 */
@Composable
fun CinematicVideoPlayer(
    videoUrl: String,
    thumbnailUrl: String,
    title: String,
    modifier: Modifier = Modifier
) {
    var isPlaying by remember(videoUrl) { mutableStateOf(false) }
    var isBuffering by remember(videoUrl) { mutableStateOf(true) }
    var isMuted by remember(videoUrl) { mutableStateOf(false) }
    var hasError by remember(videoUrl) { mutableStateOf(false) }
    var currentPosMs by remember(videoUrl) { mutableIntStateOf(0) }
    var durationMs by remember(videoUrl) { mutableIntStateOf(15000) }
    var videoViewRef by remember(videoUrl) { mutableStateOf<VideoView?>(null) }
    var mediaPlayerRef by remember(videoUrl) { mutableStateOf<MediaPlayer?>(null) }

    LaunchedEffect(isPlaying, videoViewRef) {
        while (isPlaying && videoViewRef != null) {
            try {
                val vv = videoViewRef
                if (vv != null && vv.isPlaying) {
                    currentPosMs = vv.currentPosition
                    val dur = vv.duration
                    if (dur > 0) durationMs = dur
                }
            } catch (_: Exception) {
            }
            delay(250L)
        }
    }

    DisposableEffect(videoUrl) {
        onDispose {
            try {
                videoViewRef?.stopPlayback()
            } catch (_: Exception) {
            }
            videoViewRef = null
            mediaPlayerRef = null
        }
    }

    Box(
        modifier = modifier
            .fillMaxWidth()
            .aspectRatio(16f / 9f)
            .clip(RoundedCornerShape(20.dp))
            .background(DeepBlackBg)
            .border(1.dp, GlassBorder, RoundedCornerShape(20.dp))
            .testTag("cinematic_video_player")
    ) {
        if (!hasError && videoUrl.isNotBlank()) {
            AndroidView(
                factory = { ctx ->
                    VideoView(ctx).apply {
                        videoViewRef = this
                        setVideoURI(Uri.parse(videoUrl))
                        setOnPreparedListener { mp ->
                            mediaPlayerRef = mp
                            isBuffering = false
                            mp.isLooping = true
                            val dur = mp.duration
                            if (dur > 0) durationMs = dur
                            mp.setVolume(if (isMuted) 0f else 1f, if (isMuted) 0f else 1f)
                            start()
                            isPlaying = true
                        }
                        setOnInfoListener { _, what, _ ->
                            when (what) {
                                MediaPlayer.MEDIA_INFO_BUFFERING_START -> isBuffering = true
                                MediaPlayer.MEDIA_INFO_BUFFERING_END,
                                MediaPlayer.MEDIA_INFO_VIDEO_RENDERING_START -> isBuffering = false
                            }
                            false
                        }
                        setOnErrorListener { _, _, _ ->
                            isBuffering = false
                            isPlaying = false
                            hasError = true
                            true
                        }
                    }
                },
                modifier = Modifier.fillMaxSize()
            )
        }

        // Poster thumbnail while buffering or if error
        AnimatedVisibility(
            visible = (!isPlaying && currentPosMs == 0) || hasError,
            enter = fadeIn(),
            exit = fadeOut()
        ) {
            Box(modifier = Modifier.fillMaxSize()) {
                AsyncImage(
                    model = thumbnailUrl,
                    contentDescription = title,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize()
                )
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background( DeepBlackBg.copy(alpha = 0.45f))
                )
            }
        }

        // Loading indicator
        if (isBuffering && !hasError) {
            Box(
                modifier = Modifier
                    .size(56.dp)
                    .align(Alignment.Center)
                    .clip(CircleShape)
                    .background(DeepBlackBg.copy(alpha = 0.75f)),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(
                    color = ElectricBlue,
                    strokeWidth = 3.dp,
                    modifier = Modifier.size(32.dp)
                )
            }
        }

        // Fallback if stream cannot be decoded
        if (hasError) {
            Column(
                modifier = Modifier
                    .align(Alignment.Center)
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Icon(
                    imageVector = Icons.Default.MovieCreation,
                    contentDescription = null,
                    tint = ElectricBlueLight,
                    modifier = Modifier.size(36.dp)
                )
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = "Preview Ready • Tap Retry to Stream",
                    style = MaterialTheme.typography.labelMedium,
                    color = PureWhite
                )
                Spacer(modifier = Modifier.height(8.dp))
                GradientActionButton(
                    text = "Retry Stream",
                    icon = Icons.Default.Replay,
                    onClick = {
                        hasError = false
                        isBuffering = true
                    }
                )
            }
        }

        // Bottom control bar
        Column(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
                .background(
                    Brush.verticalGradient(
                        listOf(Color.Transparent, DeepBlackBg.copy(alpha = 0.9f))
                    )
                )
                .padding(horizontal = 12.dp, vertical = 8.dp)
        ) {
            Slider(
                value = if (durationMs > 0) (currentPosMs.toFloat() / durationMs.toFloat()).coerceIn(0f, 1f) else 0f,
                onValueChange = { fraction ->
                    val target = (fraction * durationMs).toInt()
                    currentPosMs = target
                    try {
                        videoViewRef?.seekTo(target)
                    } catch (_: Exception) {
                    }
                },
                colors = SliderDefaults.colors(
                    thumbColor = ElectricBlue,
                    activeTrackColor = ElectricBlue,
                    inactiveTrackColor = PureWhite.copy(alpha = 0.25f)
                ),
                modifier = Modifier.height(20.dp)
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    IconButton(
                        onClick = {
                            val vv = videoViewRef
                            if (vv != null) {
                                if (vv.isPlaying) {
                                    vv.pause()
                                    isPlaying = false
                                } else {
                                    vv.start()
                                    isPlaying = true
                                }
                            }
                        },
                        modifier = Modifier
                            .size(40.dp)
                            .testTag("player_play_pause_button")
                    ) {
                        Icon(
                            imageVector = if (isPlaying) Icons.Default.Pause else Icons.Default.PlayArrow,
                            contentDescription = if (isPlaying) "Pause" else "Play",
                            tint = PureWhite
                        )
                    }

                    IconButton(
                        onClick = {
                            try {
                                videoViewRef?.seekTo(0)
                                videoViewRef?.start()
                                isPlaying = true
                            } catch (_: Exception) {
                            }
                        },
                        modifier = Modifier.size(40.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Replay,
                            contentDescription = "Replay",
                            tint = PureWhite
                        )
                    }

                    val curSec = (currentPosMs / 1000).coerceAtLeast(0)
                    val totSec = (durationMs / 1000).coerceAtLeast(1)
                    Text(
                        text = "%02d:%02d / %02d:%02d".format(
                            curSec / 60, curSec % 60,
                            totSec / 60, totSec % 60
                        ),
                        style = MaterialTheme.typography.labelSmall,
                        color = SlateTextSecondary
                    )
                }

                IconButton(
                    onClick = {
                        isMuted = !isMuted
                        val vol = if (isMuted) 0f else 1f
                        try {
                            mediaPlayerRef?.setVolume(vol, vol)
                        } catch (_: Exception) {
                        }
                    },
                    modifier = Modifier.size(40.dp)
                ) {
                    Icon(
                        imageVector = if (isMuted) Icons.AutoMirrored.Filled.VolumeOff else Icons.AutoMirrored.Filled.VolumeUp,
                        contentDescription = if (isMuted) "Unmute" else "Mute",
                        tint = PureWhite
                    )
                }
            }
        }
    }
}
