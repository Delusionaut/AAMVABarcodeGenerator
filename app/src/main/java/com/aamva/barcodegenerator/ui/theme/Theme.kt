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

// Light theme - clean professional white/grey palette
private val LightColorScheme = lightColorScheme(
    // Primary - slate blue-grey for professional appearance
    primary = SlateBlue50,
    onPrimary = Grey999,
    primaryContainer = SlateBlue95,
    onPrimaryContainer = SlateBlue20,
    
    // Secondary - subtle grey tones
    secondary = Grey50,
    onSecondary = Grey10,
    secondaryContainer = Grey97,
    onSecondaryContainer = Grey20,
    
    // Tertiary - accent for success states
    tertiary = Success40,
    onTertiary = Grey999,
    tertiaryContainer = Success95,
    onTertiaryContainer = Success10,
    
    // Error states
    error = Error40,
    onError = Grey999,
    errorContainer = Error95,
    onErrorContainer = Error10,
    
    // Background and surface - pure whites
    background = Grey999,
    onBackground = Grey10,
    
    surface = SurfaceWhite,
    onSurface = Grey10,
    surfaceVariant = Grey97,
    onSurfaceVariant = Grey50,
    
    // Borders and dividers
    outline = Grey85,
    outlineVariant = Grey95,
    
    // Inverse colors for cards on dark backgrounds
    inverseSurface = Grey20,
    inverseOnSurface = Grey97,
    inversePrimary = SlateBlue80,
    
    // Scrim for dialogs and sheets
    scrim = Grey10
)

// Dark theme - subtle dark grey palette
private val DarkColorScheme = darkColorScheme(
    // Primary - lighter slate for dark mode
    primary = SlateBlue80,
    onPrimary = SlateBlue20,
    primaryContainer = SlateBlue30,
    onPrimaryContainer = SlateBlue95,
    
    // Secondary
    secondary = Grey70,
    onSecondary = Grey20,
    secondaryContainer = Grey30,
    onSecondaryContainer = Grey95,
    
    // Tertiary
    tertiary = Success80,
    onTertiary = Success20,
    tertiaryContainer = Success30,
    onTertiaryContainer = Success95,
    
    // Error states
    error = Error80,
    onError = Error20,
    errorContainer = Error30,
    onErrorContainer = Error95,
    
    // Background and surface
    background = Grey10,
    onBackground = Grey97,
    
    surface = Grey10,
    onSurface = Grey97,
    surfaceVariant = Grey20,
    onSurfaceVariant = Grey70,
    
    // Borders and dividers
    outline = Grey50,
    outlineVariant = Grey30,
    
    // Inverse colors
    inverseSurface = Grey97,
    inverseOnSurface = Grey20,
    inversePrimary = SlateBlue50,
    
    // Scrim
    scrim = Grey10
)

@Composable
fun AAMVABarcodeGeneratorTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = false, // Disabled to use our custom minimalist theme
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }
        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }
    
    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            // Use primary color for status bar in light mode, surface color in dark mode
            window.statusBarColor = if (darkTheme) Grey10.toArgb() else SurfaceWhite.toArgb()
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = !darkTheme
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
