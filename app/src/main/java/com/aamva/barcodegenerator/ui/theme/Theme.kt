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

// Modern light color scheme
private val ModernLightColorScheme = lightColorScheme(
    primary = ModernPrimary,
    onPrimary = ModernOnPrimary,
    primaryContainer = ModernPrimaryLight,
    onPrimaryContainer = ModernPrimaryDark,
    secondary = ModernSecondary,
    onSecondary = ModernOnPrimary,
    tertiary = ModernAccent,
    onTertiary = ModernOnPrimary,
    background = ModernBackground,
    onBackground = ModernTextPrimary,
    surface = ModernSurface,
    onSurface = ModernOnSurface,
    surfaceVariant = GovernmentNavyPale,
    onSurfaceVariant = GovernmentGray,
    error = ModernError,
    onError = ModernOnPrimary,
    outline = ModernBorderMedium,
    outlineVariant = ModernBorderLight
)

// Modern dark color scheme
private val ModernDarkColorScheme = darkColorScheme(
    primary = ModernPrimaryLight,
    onPrimary = ModernPrimaryDark,
    primaryContainer = ModernPrimaryDark,
    onPrimaryContainer = ModernPrimaryLight,
    secondary = ModernSecondary,
    onSecondary = ModernOnPrimary,
    tertiary = ModernAccentLight,
    onTertiary = ModernPrimaryDark,
    background = ModernBackgroundDark,
    onBackground = ModernOnPrimary,
    surface = ModernSurfaceDark,
    onSurface = ModernOnPrimary,
    surfaceVariant = GovernmentNavyDark,
    onSurfaceVariant = GovernmentGrayLight,
    error = ModernError,
    onError = ModernOnPrimary,
    outline = ModernBorderDark,
    outlineVariant = ModernBorderMedium
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
        darkTheme -> ModernDarkColorScheme
        else -> ModernLightColorScheme
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