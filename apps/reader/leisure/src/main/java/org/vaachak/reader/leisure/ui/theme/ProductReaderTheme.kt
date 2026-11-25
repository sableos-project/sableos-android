package org.vaachak.reader.leisure.ui.theme

import androidx.compose.runtime.Composable
import org.sableos.design.SableTheme
import org.sableos.design.rememberGlobalSableAppearance

/** The Reader's chrome follows the SableOS system appearance. */
@Composable
fun ProductReaderTheme(content: @Composable () -> Unit) {
    SableTheme(
        appearance = rememberGlobalSableAppearance(),
        content = content,
    )
}
