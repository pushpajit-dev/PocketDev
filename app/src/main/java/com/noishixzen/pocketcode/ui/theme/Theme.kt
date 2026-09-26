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

// Modern Peach / Beige to Warm Radiant Gamut Palette
val PocketPeach = Color(0xFFF0B384)        // Warm peach / baich tone
val PocketPeachWarm = Color(0xFFFFC59B)    // Radiant baich glow
val PocketBeige = Color(0xFFE89858)        // Logo natural beige / baich
val PocketBaich = Color(0xFFE5A672)        // Soft natural baich
val PocketOrange = Color(0xFFE88239)       // Warm sunset amber (softer than neon orange)
val PocketOrangeVibrant = Color(0xFFEB8A42)// Logo radiant baich-orange gamut
val PocketOrangeDeep = Color(0xFFD96324)   // Warm terracotta amber
val PocketOrangeAmber = Color(0xFFF09C38)  // Golden amber
val PocketOrangeLight = Color(0xFFFFDEBC)  // Soft cream baich
val PocketBlue = Color(0xFF8EA8FF)
val PocketGreen = Color(0xFF68784B)           // Hazzy olive green #68784b
val HazzyGreen = Color(0xFF68784B)            // Hazzy theme primary #68784b
val HazzyGreenLight = Color(0xFF879767)       // Radiant olive green
val HazzyGreenDark = Color(0xFF4D5936)        // Deep olive green

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
    onPrimary = Color(0xFF221105),
    primaryContainer = Color(0xFF4C270E),
    onPrimaryContainer = PocketPeachWarm,
    secondary = PocketPeach,
    onSecondary = Color(0xFF221105),
    secondaryContainer = Color(0xFF522810),
    onSecondaryContainer = Color(0xFFFFE0CF),
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
    primary = PocketOrange,
    onPrimary = Color(0xFFFFFFFF),
    primaryContainer = Color(0xFFFCE9DB),
    onPrimaryContainer = Color(0xFF431F07),
    secondary = PocketPeach,
    onSecondary = Color(0xFFFFFFFF),
    secondaryContainer = Color(0xFFFBE4D2),
    onSecondaryContainer = Color(0xFF452009),
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
fun PocketTheme(
    themeMode: AppThemeMode = AppThemeMode.SYSTEM,
    amoledBlack: Boolean = false,
    content: @Composable () -> Unit
) {
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

    val activeColors = if (isDark) {
        if (amoledBlack) {
            DarkColors.copy(
                background = Color(0xFF000000),
                surface = Color(0xFF000000),
                surfaceVariant = Color(0xFF0A0C10),
            )
        } else {
            DarkColors
        }
    } else {
        LightColors
    }

    MaterialTheme(
        colorScheme = activeColors,
        typography = helveticaTypography(),
        content = content,
    )
}
