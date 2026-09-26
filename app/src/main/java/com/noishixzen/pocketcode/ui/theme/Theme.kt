package com.noishixzen.pocketcode.ui.theme

import android.app.Activity
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Typography
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.text.font.FontFamily
import androidx.core.view.WindowCompat

// Modern Peach / Beige to Vivid Orange Gamut Palette
val PocketPeach = Color(0xFFF7A56C)        // Peach / Baich
val PocketPeachWarm = Color(0xFFFFB37C)    // Warm peach glow
val PocketBeige = Color(0xFFE89858)        // Logo natural beige
val PocketOrange = Color(0xFFFF6F00)       // Vivid modern gamut orange
val PocketOrangeVibrant = Color(0xFFFF7A18)// High-gamut radiant orange
val PocketOrangeDeep = Color(0xFFF4511E)   // Warm sunset vermilion
val PocketOrangeAmber = Color(0xFFFFA000)  // Golden amber
val PocketOrangeLight = Color(0xFFFFCC80)  // Soft peach/cream
val PocketBlue = Color(0xFF8EA8FF)
val PocketGreen = Color(0xFF69D69E)

val PocketBackgroundDark = Color(0xFF0C0E12)
val PocketSurfaceDark = Color(0xFF13171F)
val PocketSurfaceVariantDark = Color(0xFF1B202A)
val PocketOutlineDark = Color(0xFF2A313E)

val PocketBackgroundLight = Color(0xFFFDFBF9)
val PocketSurfaceLight = Color(0xFFFFFFFF)
val PocketSurfaceVariantLight = Color(0xFFF7F3EE)
val PocketOutlineLight = Color(0xFFE5DDD5)

// Gradients (Peach/Beige to Radiant Orange)
val OrangeGradient = Brush.horizontalGradient(
    colors = listOf(PocketPeach, PocketOrangeVibrant, PocketOrangeDeep)
)
val OrangeGradientVertical = Brush.verticalGradient(
    colors = listOf(PocketPeach, PocketOrangeVibrant, PocketOrangeDeep)
)
val PeachOrangeGradient = Brush.horizontalGradient(
    colors = listOf(PocketPeachWarm, PocketOrangeVibrant)
)
val OrangeSubtleGradientDark = Brush.linearGradient(
    colors = listOf(Color(0xFF23150B), Color(0xFF13171F))
)
val OrangeSubtleGradientLight = Brush.linearGradient(
    colors = listOf(Color(0xFFFFF6EE), Color(0xFFFFFFFF))
)

// Font Family - Helvetica
val HelveticaFontFamily = FontFamily.SansSerif

fun helveticaTypography(): Typography {
    val default = Typography()
    return Typography(
        displayLarge = default.displayLarge.copy(fontFamily = HelveticaFontFamily),
        displayMedium = default.displayMedium.copy(fontFamily = HelveticaFontFamily),
        displaySmall = default.displaySmall.copy(fontFamily = HelveticaFontFamily),
        headlineLarge = default.headlineLarge.copy(fontFamily = HelveticaFontFamily),
        headlineMedium = default.headlineMedium.copy(fontFamily = HelveticaFontFamily),
        headlineSmall = default.headlineSmall.copy(fontFamily = HelveticaFontFamily),
        titleLarge = default.titleLarge.copy(fontFamily = HelveticaFontFamily),
        titleMedium = default.titleMedium.copy(fontFamily = HelveticaFontFamily),
        titleSmall = default.titleSmall.copy(fontFamily = HelveticaFontFamily),
        bodyLarge = default.bodyLarge.copy(fontFamily = HelveticaFontFamily),
        bodyMedium = default.bodyMedium.copy(fontFamily = HelveticaFontFamily),
        bodySmall = default.bodySmall.copy(fontFamily = HelveticaFontFamily),
        labelLarge = default.labelLarge.copy(fontFamily = HelveticaFontFamily),
        labelMedium = default.labelMedium.copy(fontFamily = HelveticaFontFamily),
        labelSmall = default.labelSmall.copy(fontFamily = HelveticaFontFamily),
    )
}

private val DarkColors = darkColorScheme(
    primary = PocketOrangeVibrant,
    onPrimary = Color(0xFF1C0A00),
    primaryContainer = Color(0xFF4A1E00),
    onPrimaryContainer = PocketPeachWarm,
    secondary = PocketPeach,
    onSecondary = Color(0xFF200C00),
    secondaryContainer = Color(0xFF4E1602),
    onSecondaryContainer = Color(0xFFFFDBD0),
    tertiary = PocketGreen,
    onTertiary = Color(0xFF00391E),
    background = PocketBackgroundDark,
    onBackground = Color(0xFFE5E7EB),
    surface = PocketSurfaceDark,
    onSurface = Color(0xFFE5E7EB),
    surfaceVariant = PocketSurfaceVariantDark,
    onSurfaceVariant = Color(0xFF9CA3AF),
    outline = PocketOutlineDark,
    outlineVariant = Color(0xFF374151),
)

private val LightColors = lightColorScheme(
    primary = Color(0xFFE65100),
    onPrimary = Color(0xFFFFFFFF),
    primaryContainer = Color(0xFFFFE8D6),
    onPrimaryContainer = Color(0xFF3E1200),
    secondary = Color(0xFFF4511E),
    onSecondary = Color(0xFFFFFFFF),
    secondaryContainer = Color(0xFFFFCCBC),
    onSecondaryContainer = Color(0xFF380E00),
    tertiary = Color(0xFF16A34A),
    onTertiary = Color(0xFFFFFFFF),
    background = PocketBackgroundLight,
    onBackground = Color(0xFF111827),
    surface = PocketSurfaceLight,
    onSurface = Color(0xFF111827),
    surfaceVariant = PocketSurfaceVariantLight,
    onSurfaceVariant = Color(0xFF4B5563),
    outline = PocketOutlineLight,
    outlineVariant = Color(0xFFE5E7EB),
)

enum class AppThemeMode { SYSTEM, DARK, LIGHT }

@Composable
fun PocketTheme(themeMode: AppThemeMode = AppThemeMode.SYSTEM, content: @Composable () -> Unit) {
    val isDark = when (themeMode) {
        AppThemeMode.DARK -> true
        AppThemeMode.LIGHT -> false
        AppThemeMode.SYSTEM -> isSystemInDarkTheme()
    }

    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as? Activity)?.window ?: return@SideEffect
            val insetsController = WindowCompat.getInsetsController(window, view)
            insetsController.isAppearanceLightStatusBars = !isDark
            insetsController.isAppearanceLightNavigationBars = !isDark
        }
    }

    MaterialTheme(
        colorScheme = if (isDark) DarkColors else LightColors,
        typography = helveticaTypography(),
        content = content,
    )
}
