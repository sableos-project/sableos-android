/*
 * ConnectBot: simple, powerful, open-source SSH client for Android
 * Copyright 2025-2026 Kenny Root
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.connectbot.ui.theme

import android.app.Activity
import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.ColorScheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat
import org.connectbot.util.ThemeMode

// SableOS: the Sable colours (sable-src/apps/r8/connectbot/apply_sable_flavor.py).
private val lightScheme = lightColorScheme(
    primary = Color(0xFF1A6FA0),
    onPrimary = Color(0xFFFFFFFF),
    primaryContainer = Color(0xFFE9EDF2),
    onPrimaryContainer = Color(0xFF12171C),
    secondary = Color(0xFF1A6FA0),
    onSecondary = Color(0xFFFFFFFF),
    secondaryContainer = Color(0xFFE9EDF2),
    onSecondaryContainer = Color(0xFF12171C),
    tertiary = Color(0xFF1A6FA0),
    onTertiary = Color(0xFFFFFFFF),
    tertiaryContainer = Color(0xFFE9EDF2),
    onTertiaryContainer = Color(0xFF12171C),
    error = Color(0xFFBA1A1A),
    onError = Color(0xFFFFFFFF),
    errorContainer = Color(0xFFFFDAD6),
    onErrorContainer = Color(0xFF410002),
    background = Color(0xFFF5F7F9),
    onBackground = Color(0xFF12171C),
    surface = Color(0xFFFFFFFF),
    onSurface = Color(0xFF12171C),
    surfaceVariant = Color(0xFFE9EDF2),
    onSurfaceVariant = Color(0xFF515B66),
    outline = Color(0xFF8A939C),
    outlineVariant = Color(0xFFD5DBE1),
    scrim = Color(0xFF000000),
    inverseSurface = Color(0xFF12171C),
    inverseOnSurface = Color(0xFFFFFFFF),
    inversePrimary = Color(0xFF2E9BD6),
    surfaceDim = Color(0xFFE9EDF2),
    surfaceBright = Color(0xFFFFFFFF),
    surfaceContainerLowest = Color(0xFFFFFFFF),
    surfaceContainerLow = Color(0xFFF5F7F9),
    surfaceContainer = Color(0xFFF0F3F6),
    surfaceContainerHigh = Color(0xFFE9EDF2),
    surfaceContainerHighest = Color(0xFFE2E7EC),
)

private val darkScheme = darkColorScheme(
    primary = Color(0xFF2E9BD6),
    onPrimary = Color(0xFF000000),
    primaryContainer = Color(0xFF262626),
    onPrimaryContainer = Color(0xFFFFFFFF),
    secondary = Color(0xFF2E9BD6),
    onSecondary = Color(0xFF000000),
    secondaryContainer = Color(0xFF262626),
    onSecondaryContainer = Color(0xFFFFFFFF),
    tertiary = Color(0xFF2E9BD6),
    onTertiary = Color(0xFF000000),
    tertiaryContainer = Color(0xFF262626),
    onTertiaryContainer = Color(0xFFFFFFFF),
    error = Color(0xFFE0463C),
    onError = Color(0xFF000000),
    errorContainer = Color(0xFF3A1210),
    onErrorContainer = Color(0xFFFFFFFF),
    background = Color(0xFF000000),
    onBackground = Color(0xFFFFFFFF),
    surface = Color(0xFF000000),
    onSurface = Color(0xFFFFFFFF),
    surfaceVariant = Color(0xFF262626),
    onSurfaceVariant = Color(0xFFA6A6A6),
    outline = Color(0xFF595959),
    outlineVariant = Color(0xFF262626),
    scrim = Color(0xFF000000),
    inverseSurface = Color(0xFFFFFFFF),
    inverseOnSurface = Color(0xFF000000),
    inversePrimary = Color(0xFF1A6FA0),
    surfaceDim = Color(0xFF000000),
    surfaceBright = Color(0xFF262626),
    surfaceContainerLowest = Color(0xFF000000),
    surfaceContainerLow = Color(0xFF0A0A0A),
    surfaceContainer = Color(0xFF141414),
    surfaceContainerHigh = Color(0xFF1A1A1A),
    surfaceContainerHighest = Color(0xFF262626),
)

@Composable
fun ConnectBotTheme(
    themeMode: ThemeMode = ThemeMode.SYSTEM,
    // Dynamic color is available on Android 12+
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit,
) {
    val darkTheme = when (themeMode) {
        ThemeMode.LIGHT -> false
        ThemeMode.DARK -> true
        ThemeMode.SYSTEM -> isSystemInDarkTheme()
    }
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }

        darkTheme -> darkScheme

        else -> lightScheme
    }

    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            window.statusBarColor = colorScheme.background.toArgb()
            WindowCompat.getInsetsController(window, view).apply {
                isAppearanceLightStatusBars = !darkTheme
            }
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = AppTypography,
        content = content,
    )
}

/**
 * Terminal-specific colors for overlays that appear over the terminal view.
 * These are independent of light/dark theme since the terminal background is always dark.
 */
data class TerminalColors(
    val overlayBackground: Color,
    val overlayText: Color,
    val overlayTextSecondary: Color,
)

private val terminalColors = TerminalColors(
    overlayBackground = TerminalOverlayBackground,
    overlayText = TerminalOverlayText,
    overlayTextSecondary = TerminalOverlayTextSecondary,
)

/**
 * Access terminal-specific colors via MaterialTheme.colorScheme.terminal
 */
val ColorScheme.terminal: TerminalColors
    @Composable
    @ReadOnlyComposable
    get() = terminalColors
