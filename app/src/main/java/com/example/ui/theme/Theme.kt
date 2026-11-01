package com.example.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable

private val VishnuDarkColorScheme = darkColorScheme(
    primary = ElectricBlue,
    onPrimary = PureWhite,
    primaryContainer = DarkNavyElevated,
    onPrimaryContainer = PureWhite,
    secondary = NeonPurple,
    onSecondary = PureWhite,
    secondaryContainer = DarkNavyElevated,
    onSecondaryContainer = PureWhite,
    tertiary = ElectricBlueLight,
    onTertiary = DeepBlackBg,
    background = DeepBlackBg,
    onBackground = PureWhite,
    surface = DarkNavySurface,
    onSurface = PureWhite,
    surfaceVariant = DarkNavyElevated,
    onSurfaceVariant = SlateTextSecondary,
    error = CrimsonDanger,
    onError = PureWhite,
    outline = GlassBorder
)

@Composable
fun MyApplicationTheme(
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = VishnuDarkColorScheme,
        typography = Typography,
        content = content
    )
}
