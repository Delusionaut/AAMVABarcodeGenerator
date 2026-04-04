package com.aamva.barcodegenerator.ui.theme

import android.app.Activity
import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

private val LightColorScheme = lightColorScheme(
    primary = ModernPrimary,
    onPrimary = ModernOnPrimary,
    primaryContainer = ModernPrimaryLight,
    onPrimaryContainer = ModernPrimaryDark,
    secondary = ModernSecondary,
    onSecondary = ModernOnSecondary,
    secondaryContainer = ModernSecondaryLight,
    onSecondaryContainer = ModernSecondaryDark,
    tertiary = ModernAccent,
    onTertiary = ModernOnPrimary,
    tertiaryContainer = ModernAccentLight,
    onTertiaryContainer = ModernAccent,
    background = ModernBackground,
    onBackground = ModernTextPrimary,
    surface = ModernSurface,
    onSurface = ModernOnSurface,
    surfaceVariant = ModernSurfaceVariant,
    onSurfaceVariant = ModernTextSecondary,
    error = ModernError,
    onError = ModernOnPrimary,
    errorContainer = ModernErrorLight,
    onErrorContainer = ModernError,
    outline = ModernBorderMedium,
    outlineVariant = ModernBorderLight,
    scrim = ModernShadowDark
)

private val DarkColorScheme = darkColorScheme(
    primary = ModernPrimaryLight,
    onPrimary = ModernPrimaryDark,
    primaryContainer = ModernPrimaryDark,
    onPrimaryContainer = ModernPrimaryLight,
    secondary = ModernSecondaryLight,
    onSecondary = ModernSecondaryDark,
    secondaryContainer = ModernSecondaryDark,
    onSecondaryContainer = ModernSecondaryLight,
    tertiary = ModernAccentLight,
    onTertiary = ModernPrimaryDark,
    tertiaryContainer = ModernAccent,
    onTertiaryContainer = ModernAccentLight,
    background = ModernBackgroundDark,
    onBackground = ModernTextPrimary,
    surface = ModernSurfaceDark,
    onSurface = ModernOnSurface,
    surfaceVariant = ModernSurfaceDark,
    onSurfaceVariant = ModernTextSecondary,
    error = ModernErrorLight,
    onError = ModernError,
    errorContainer = ModernError,
    onErrorContainer = ModernErrorLight,
    outline = ModernBorderDark,
    outlineVariant = ModernBorderMedium,
    scrim = androidx.compose.ui.graphics.Color.Black.copy(alpha = 0.8f)
)

@Composable
fun AAMVABarcodeGeneratorTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = true,
    content: @Composable () -> Unit
) {
    val context = LocalContext.current
    val view = LocalView.current
    
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            if (darkTheme) dynamicDarkColorScheme(context)
            else dynamicLightColorScheme(context)
        }
        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }
    
    if (!view.isInEditMode) {
        SideEffect {
            val window = (context as Activity).window
            window.statusBarColor = colorScheme.primary.toArgb()
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = !darkTheme
            WindowCompat.setDecorFitsSystemWindows(window, false)
        }
    }
    
    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}