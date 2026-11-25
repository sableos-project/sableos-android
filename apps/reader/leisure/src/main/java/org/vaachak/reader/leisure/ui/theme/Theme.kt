/*
 *  Copyright (c) 2026 Piyush Daiya
 *  *
 *  * Permission is hereby granted, free of charge, to any person obtaining a copy
 *  * of this software and associated documentation files (the "Software"), to deal
 *  * in the Software without restriction, including without limitation the rights
 *  * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 *  * copies of the Software, and to permit persons to whom the Software is
 *  * furnished to do so, subject to the following conditions:
 *  *
 *  * The above copyright notice and this permission notice shall be included in all
 *  * copies or substantial portions of the Software.
 *  *
 *  * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 *  * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 *  * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 *  * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 *  * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 *  * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 *  * SOFTWARE.
 */

package org.vaachak.reader.leisure.ui.theme

import android.app.Activity
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat
import org.vaachak.reader.core.domain.model.ThemeMode

@Composable
fun VaachakTheme(
    themeMode: ThemeMode = ThemeMode.LIGHT,
    content: @Composable () -> Unit
) {
    val colorScheme = when (themeMode) {
        ThemeMode.DARK -> darkColorScheme(
            primary = PureWhite,
            onPrimary = PureBlack,
            secondary = Color(0xFFB0B0B0),
            background = PureBlack,
            surface = DarkSurface, // Uses your Color.kt
            onBackground = PureWhite,
            onSurface = PureWhite,
            surfaceVariant = Color(0xFF222222),
            onSurfaceVariant = Color(0xFFCCCCCC),
            outline = Color(0xFF444444),
            surfaceContainerLowest = PureBlack,
            surfaceContainerLow = DarkSurface,
            surfaceContainer = DarkSurface,
            surfaceContainerHigh = DarkSurface,
            surfaceContainerHighest = Color(0xFF222222)
        )
        ThemeMode.LIGHT -> lightColorScheme(
            primary = PureBlack,
            onPrimary = PureWhite,
            background = SoftWhite, // Uses your Color.kt
            surface = PureWhite,
            onBackground = PureBlack,
            onSurface = PureBlack,
            surfaceVariant = LightGray, // Uses your Color.kt
            onSurfaceVariant = Color.DarkGray, // Keep subtitles readable in Light mode
            outline = Color.LightGray,
            surfaceContainerLowest = PureWhite,
            surfaceContainerLow = PureWhite,
            surfaceContainer = PureWhite,
            surfaceContainerHigh = PureWhite,
            surfaceContainerHighest = LightGray
        )
    }

    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window

            // 1. Get the Controller
            val insetsController = WindowCompat.getInsetsController(window, view)

            // 2. Determine Icon Color
            // If NOT Dark Mode, we want Dark Icons (so they show up on a light background).
            val useDarkIcons = (themeMode != ThemeMode.DARK)

            // 3. Set Icon Colors (No Deprecation Warnings!)
            insetsController.isAppearanceLightStatusBars = useDarkIcons
            insetsController.isAppearanceLightNavigationBars = useDarkIcons
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography, // Uses your Type.kt
        content = content
    )
}
