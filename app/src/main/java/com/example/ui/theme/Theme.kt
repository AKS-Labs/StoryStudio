package com.example.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext

private val AndroidifyLightColorScheme = lightColorScheme(
    primary = AndroidifyGreen,
    onPrimary = AndroidifyBlack,
    primaryContainer = AndroidifyGreen.copy(alpha = 0.2f),
    onPrimaryContainer = AndroidifyDarkGreen,
    secondary = AndroidifyBlue,
    onSecondary = Color.White,
    secondaryContainer = AndroidifyBlue.copy(alpha = 0.15f),
    onSecondaryContainer = AndroidifyBlue,
    tertiary = AndroidifyCoral,
    onTertiary = Color.White,
    background = AndroidifyPaleMint,
    onBackground = AndroidifyBlack,
    surface = AndroidifySurface,
    onSurface = AndroidifyBlack,
    surfaceVariant = AndroidifySurfaceVariant,
    onSurfaceVariant = AndroidifyTextSecondary,
    outline = AndroidifyOutline
)

private val AndroidifyDarkColorScheme = darkColorScheme(
    primary = AndroidifyGreen,
    onPrimary = Color.Black,
    secondary = AndroidifyBlue,
    onSecondary = Color.White,
    tertiary = AndroidifyCoral,
    background = Color(0xFF121413),
    onBackground = Color(0xFFE2E4E1),
    surface = Color(0xFF1A1C1B),
    onSurface = Color(0xFFE2E4E1)
)

@Composable
fun StoryCanvasTheme(
    darkTheme: Boolean = false, // Default to clean Androidify light mode
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }
        darkTheme -> AndroidifyDarkColorScheme
        else -> AndroidifyLightColorScheme
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}

@Composable
fun MyApplicationTheme(
    darkTheme: Boolean = false,
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit
) = StoryCanvasTheme(darkTheme = darkTheme, dynamicColor = dynamicColor, content = content)
