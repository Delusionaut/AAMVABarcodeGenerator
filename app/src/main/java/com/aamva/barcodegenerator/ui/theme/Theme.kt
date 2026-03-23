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

// DMV Light Theme - Compact government form aesthetic
private val GovernmentLightColorScheme = lightColorScheme(
    primary = GovernmentNavy,
    onPrimary = OfficialWhite,
    primaryContainer = GovernmentNavyPale,
    onPrimaryContainer = GovernmentNavyDark,
    
    secondary = GovernmentGray,
    onSecondary = OfficialWhite,
    secondaryContainer = GovernmentGrayPale,
    onSecondaryContainer = GovernmentGrayDark,
    
    tertiary = GovernmentNavyLight,
    onTertiary = OfficialWhite,
    tertiaryContainer = GovernmentGrayPale,
    onTertiaryContainer = GovernmentNavy,
    
    error = GovernmentRed,
    onError = OfficialWhite,
    errorContainer = GovernmentRedLight,
    onErrorContainer = GovernmentRed,
    
    background = OfficialLight,
    onBackground = GovernmentGrayDark,
    
    surface = OfficialWhite,
    onSurface = GovernmentGrayDark,
    surfaceVariant = GovernmentGrayPale,
    onSurfaceVariant = GovernmentGray,
    
    outline = OfficialBorder,
    outlineVariant = OfficialDivider,
    
    inverseSurface = GovernmentGrayDark,
    inverseOnSurface = GovernmentGrayPale,
    inversePrimary = GovernmentNavyLight,
    
    scrim = Color.Black.copy(alpha = 0.32f)
)

// Government Dark Theme
private val GovernmentDarkColorScheme = darkColorScheme(
    primary = GovernmentNavyLight,
    onPrimary = GovernmentNavyDark,
    primaryContainer = GovernmentNavy,
    onPrimaryContainer = GovernmentNavyPale,
    
    secondary = GovernmentGrayLight,
    onSecondary = GovernmentGrayDark,
    secondaryContainer = GovernmentGray,
    onSecondaryContainer = GovernmentGrayPale,
    
    tertiary = GovernmentNavyPale,
    onTertiary = GovernmentNavyDark,
    tertiaryContainer = GovernmentNavyDark,
    onTertiaryContainer = GovernmentNavyPale,
    
    error = Color(0xFFFF8A80),
    onError = Color(0xFF690005),
    errorContainer = Color(0xFF93000A),
    onErrorContainer = Color(0xFFFFDAD6),
    
    background = GovernmentGrayDark,
    onBackground = GovernmentGrayPale,
    
    surface = GovernmentGrayDark,
    onSurface = GovernmentGrayPale,
    surfaceVariant = GovernmentGray,
    onSurfaceVariant = GovernmentGrayLight,
    
    outline = GovernmentGray,
    outlineVariant = GovernmentGrayDark,
    
    inverseSurface = GovernmentGrayPale,
    inverseOnSurface = GovernmentGrayDark,
    inversePrimary = GovernmentNavy,
    
    scrim = Color.Black.copy(alpha = 0.32f)
)

@Composable
fun AAMVABarcodeGeneratorTheme(
    darkTheme: Boolean = false,
    dynamicColor: Boolean = true,
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            dynamicLightColorScheme(context)
        }
        else -> GovernmentLightColorScheme
    }
    
    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            window.statusBarColor = GovernmentNavy.toArgb()
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = true
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}