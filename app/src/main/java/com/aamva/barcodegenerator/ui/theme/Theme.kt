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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

// 🎨 **INDUSTRY-LEADING MODERN THEME SYSTEM**
// Glassmorphism + Gradients + Smooth Animations

// **LIGHT THEME - Clean & Professional**
private val ModernLightColorScheme = lightColorScheme(
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

// **DARK THEME - True Blacks & Deep Grays**
private val ModernDarkColorScheme = darkColorScheme(
    primary = ModernPrimaryLight,
    onPrimary = ModernPrimaryDark,
    primaryContainer = ModernPrimaryDark,
    onPrimaryContainer = ModernPrimaryLight,
    
    secondary = ModernSecondaryLight,
    onSecondary = ModernSecondaryDark,
    secondaryContainer = ModernSecondaryDark,
    onSecondaryContainer = ModernSecondaryLight,
    
    tertiary = ModernAccentLight,
    onTertiary = ModernAccentDark,
    tertiaryContainer = ModernAccentDark,
    onTertiaryContainer = ModernAccentLight,
    
    background = ModernBackgroundDark,
    onBackground = ModernTextPrimary,
    surface = ModernSurfaceDark,
    onSurface = ModernOnSurface,
    surfaceVariant = ModernSurface,
    onSurfaceVariant = ModernTextSecondary,
    
    error = ModernErrorLight,
    onError = ModernError,
    errorContainer = ModernError,
    onErrorContainer = ModernErrorLight,
    
    outline = ModernBorderDark,
    outlineVariant = ModernBorderMedium,
    
    scrim = Color.Black.copy(alpha = 0.8f)
)

// **GLASSMORPHISM CARD STYLES**
object GlassEffects {
    // Glass card with blur effect
    val GlassCardLight = Color(0x80FFFFFF)
    val GlassCardDark = Color(0x801E293B)
    
    // Frosted glass for overlays
    val FrostedOverlayLight = Color(0x60FFFFFF)
    val FrostedOverlayDark = Color(0x600F172A)
    
    // Gradient glass
    val GradientGlass = Color(0x407C3AED)
}

@Composable
fun AAMVABarcodeGeneratorTheme(
    darkTheme: Boolean = false,
    dynamicColor: Boolean = true,
    content: @Composable () -> Unit
) {
    val context = LocalContext.current
    val view = LocalView.current
    
    // Choose color scheme
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            if (darkTheme) dynamicDarkColorScheme(context)
            else dynamicLightColorScheme(context)
        }
        darkTheme -> ModernDarkColorScheme
        else -> ModernLightColorScheme
    }
    
    // Modern window styling
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
