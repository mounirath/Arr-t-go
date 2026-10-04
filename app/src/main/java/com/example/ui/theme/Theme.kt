package com.example.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val DarkColorScheme = darkColorScheme(
    primary = ArrivaIndigo,
    onPrimary = Color.White,
    primaryContainer = Color(0xFF3730A3),
    onPrimaryContainer = Color(0xFFE0E7FF),
    secondary = ArrivaCyan,
    onSecondary = Color(0xFF042F2E),
    secondaryContainer = Color(0xFF164E63),
    onSecondaryContainer = Color(0xFFCFFAFE),
    tertiary = ArrivaCoral,
    onTertiary = Color.White,
    background = ArrivaDarkBg,
    onBackground = ArrivaDarkText,
    surface = ArrivaDarkSurface,
    onSurface = ArrivaDarkText,
    surfaceVariant = ArrivaDarkSurfaceVariant,
    onSurfaceVariant = ArrivaDarkTextDim,
    outline = Color(0xFF334155),
    error = ArrivaCoral
)

private val LightColorScheme = lightColorScheme(
    primary = ArrivaIndigo,
    onPrimary = Color.White,
    primaryContainer = Color(0xFFEEF2FF),
    onPrimaryContainer = Color(0xFF312E81),
    secondary = Color(0xFF0284C7),
    onSecondary = Color.White,
    secondaryContainer = Color(0xFFE0F2FE),
    onSecondaryContainer = Color(0xFF0369A1),
    tertiary = ArrivaCoral,
    onTertiary = Color.White,
    background = ArrivaLightBg,
    onBackground = ArrivaLightText,
    surface = ArrivaLightSurface,
    onSurface = ArrivaLightText,
    surfaceVariant = ArrivaLightSurfaceVariant,
    onSurfaceVariant = ArrivaLightTextDim,
    outline = Color(0xFFCBD5E1),
    error = ArrivaCoral
)

@Composable
fun ArrivaTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}

// Backward compatibility alias for template
@Composable
fun MyApplicationTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit
) {
    ArrivaTheme(darkTheme = darkTheme, content = content)
}
