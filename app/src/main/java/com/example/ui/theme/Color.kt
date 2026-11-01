package com.example.ui.theme

import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color

// Core VISHNU EDIT AI Palette (Requested Spec)
val DeepBlackBg = Color(0xFF080B12)
val DarkNavySurface = Color(0xFF101827)
val DarkNavyElevated = Color(0xFF162033)
val DarkNavyGlass = Color(0xCC101827)
val ElectricBlue = Color(0xFF287BFF)
val ElectricBlueLight = Color(0xFF5CA0FF)
val NeonPurple = Color(0xFF9146FF)
val NeonPurpleLight = Color(0xFFB47CFF)

// Typography & Borders
val PureWhite = Color(0xFFFFFFFF)
val SlateTextSecondary = Color(0xFF94A3B8)
val SlateTextMuted = Color(0xFF64748B)
val GlassBorder = Color(0x33287BFF)
val PurpleGlassBorder = Color(0x409146FF)
val SubtleDivider = Color(0xFF1E293B)

// Status Accents
val EmeraldSuccess = Color(0xFF10B981)
val AmberWarning = Color(0xFFF59E0B)
val CrimsonDanger = Color(0xFFEF4444)

// Premium Gradients
val VishnuPrimaryGradient = Brush.horizontalGradient(
    colors = listOf(ElectricBlue, NeonPurple)
)

val VishnuVerticalGradient = Brush.verticalGradient(
    colors = listOf(ElectricBlue, NeonPurple)
)

val VishnuCardGlassBrush = Brush.linearGradient(
    colors = listOf(
        Color(0xFF141E30),
        Color(0xFF0D1422)
    )
)

val VishnuHeroOverlayBrush = Brush.verticalGradient(
    colors = listOf(
        Color(0x1A080B12),
        Color(0x99080B12),
        Color(0xFF080B12)
    )
)
