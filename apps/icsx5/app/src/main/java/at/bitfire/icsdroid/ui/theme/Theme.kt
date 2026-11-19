/*
 * Copyright © All Contributors. See LICENSE and AUTHORS in the root directory for details.
 */

package at.bitfire.icsdroid.ui.theme

import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionContext
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalView
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.LifecycleResumeEffect
import at.bitfire.icsdroid.model.ThemeModel
import at.bitfire.icsdroid.ui.ForegroundTracker

// SableOS: the Sable colours in place of ICSx5's (sable-src/apps/r8/icsx5/apply_sable_flavor.py).
private val DarkColors = darkColorScheme(
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

private val LightColors = lightColorScheme(
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

@Composable
fun AppTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = if (darkTheme) DarkColors else LightColors,
        content = content
    )

    // Track whether the app is in the foreground
    val view = LocalView.current
    LifecycleResumeEffect(view) {
        ForegroundTracker.onResume()
        onPauseOrDispose {
            ForegroundTracker.onPaused()
        }
    }
}

/**
 * Composes the given composable into the given activity. The content will become the root view of
 * the given activity.
 * This is roughly equivalent to calling [ComponentActivity.setContentView] with a ComposeView i.e.:
 * ```kotlin
 * setContentView(
 *   ComposeView(this).apply {
 *     setContent {
 *       MyComposableContent()
 *     }
 *   }
 * )
 * ```
 *
 * Then, applies [AppTheme] to the UI.
 *
 * @param parent The parent composition reference to coordinate scheduling of composition updates
 * @param darkTheme Calculates whether the UI should be shown in light or dark theme.
 * @param content A `@Composable` function declaring the UI contents
 */
fun ComponentActivity.setContentThemed(
    parent: CompositionContext? = null,
    darkTheme: @Composable () -> Boolean = {
        val model = hiltViewModel<ThemeModel>()
        val forceDarkTheme by model.forceDarkMode.collectAsState()
        forceDarkTheme || isSystemInDarkTheme()
    },
    content: @Composable () -> Unit
) {
    setContent(parent) {
        AppTheme(darkTheme = darkTheme()) {
            content()
        }
    }
}
