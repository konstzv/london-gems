package com.londongemsapp.presentation.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext

private val LightColorScheme = lightColorScheme(
    primary = BluePrimary,
    onPrimary = White,
    primaryContainer = BluePrimaryLight,
    onPrimaryContainer = White,
    secondary = GoldAccent,
    onSecondary = BluePrimaryDark,
    secondaryContainer = GoldAccentLight,
    onSecondaryContainer = BluePrimaryDark,
    tertiary = TealTertiary,
    onTertiary = White,
    tertiaryContainer = TealTertiaryLight,
    onTertiaryContainer = BluePrimaryDark,
    background = GrayBackground,
    onBackground = GrayOnSurface,
    surface = GraySurface,
    onSurface = GrayOnSurface,
    surfaceVariant = GrayBackground,
    onSurfaceVariant = GrayOnSurfaceVariant,
    outline = GrayOutline,
    error = ErrorRed,
    onError = White,
    errorContainer = ErrorContainerLight,
    onErrorContainer = ErrorRed
)

private val DarkColorScheme = darkColorScheme(
    primary = GoldAccentLight,
    onPrimary = BluePrimaryDark,
    primaryContainer = BluePrimary,
    onPrimaryContainer = GoldAccentLight,
    secondary = GoldAccent,
    onSecondary = BluePrimaryDark,
    secondaryContainer = GoldAccentDark,
    onSecondaryContainer = GoldAccentLight,
    tertiary = TealTertiaryLight,
    onTertiary = TealTertiaryDark,
    tertiaryContainer = TealTertiaryDark,
    onTertiaryContainer = TealTertiaryLight,
    background = DarkBackground,
    onBackground = DarkOnSurface,
    surface = DarkSurface,
    onSurface = DarkOnSurface,
    surfaceVariant = DarkSurfaceVariant,
    onSurfaceVariant = DarkOnSurfaceVariant,
    outline = DarkOutline,
    error = ErrorRedDark,
    onError = ErrorContainerDark,
    errorContainer = ErrorContainerDark,
    onErrorContainer = ErrorRedDark
)

@Composable
fun LondonGemsTheme(
    darkTheme: Boolean? = null,
    dynamicColor: Boolean = true,
    content: @Composable () -> Unit
) {
    val isDark = darkTheme ?: isSystemInDarkTheme()
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (isDark) dynamicDarkColorScheme(context)
            else dynamicLightColorScheme(context)
        }
        isDark -> DarkColorScheme
        else -> LightColorScheme
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
