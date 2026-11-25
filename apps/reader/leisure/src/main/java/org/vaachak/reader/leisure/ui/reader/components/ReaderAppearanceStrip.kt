@file:OptIn(org.readium.r2.shared.ExperimentalReadiumApi::class)

package org.vaachak.reader.leisure.ui.reader.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.focusable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.input.key.KeyEventType
import androidx.compose.ui.input.key.key
import androidx.compose.ui.input.key.onPreviewKeyEvent
import androidx.compose.ui.input.key.type
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import androidx.activity.compose.BackHandler
import org.readium.r2.navigator.epub.EpubPreferences

/** Rows with at most this many choices show every choice; longer rows show the current one between arrows. */
private const val SEGMENTED_MAX = 4

/**
 * The Display and Layout settings as a strip along the bottom of a square window, so the book stays visible while
 * the font, size, spacing and margins change. Up and Down pick a row, Left and Right change it, Tab switches between
 * Display and Layout, P turns publisher styles on or off, Enter keeps the look and Esc puts back what the book had.
 */
@Composable
fun ReaderAppearanceStrip(
    prefs: EpubPreferences,
    onChange: (EpubPreferences) -> Unit,
    onDone: () -> Unit,
    onAllSettings: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val initial = remember { prefs }
    var pageIndex by remember { mutableIntStateOf(0) }
    var rowIndex by remember { mutableIntStateOf(0) }
    val focus = remember { FocusRequester() }
    val publisherStyles = prefs.publisherStyles ?: true
    val page = AppearanceStripModel.pages[pageIndex]
    // With publisher styles on, only the theme and the size apply; every other row waits for P.
    fun rowEnabled(page: StripPage, index: Int) = !publisherStyles || page.rows[index].label in ALWAYS_ON_ROWS

    fun undo() {
        if (prefs != initial) onChange(initial)
        onDone()
    }

    LaunchedEffect(Unit) { runCatching { focus.requestFocus() } }
    BackHandler { undo() }

    Box(modifier.fillMaxSize().zIndex(10f), contentAlignment = Alignment.BottomCenter) {
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .focusRequester(focus)
                .focusable()
                .semantics { contentDescription = "Reading look. ${page.title} page. Up and Down pick a setting, Left and Right change it." }
                .onPreviewKeyEvent { event ->
                    if (event.type != KeyEventType.KeyDown) return@onPreviewKeyEvent false
                    val rows = page.rows
                    when (event.key) {
                        Key.DirectionUp -> { rowIndex = (rowIndex - 1).coerceAtLeast(0); true }
                        Key.DirectionDown -> { rowIndex = (rowIndex + 1).coerceAtMost(rows.lastIndex); true }
                        Key.DirectionLeft, Key.DirectionRight -> {
                            val delta = if (event.key == Key.DirectionLeft) -1 else 1
                            if (rowEnabled(page, rowIndex)) onChange(AppearanceStripModel.step(rows[rowIndex], prefs, delta))
                            true
                        }
                        Key.Tab -> {
                            pageIndex = (pageIndex + 1) % AppearanceStripModel.pages.size
                            rowIndex = 0
                            true
                        }
                        Key.P -> { onChange(prefs.copy(publisherStyles = !publisherStyles)); true }
                        Key.Enter, Key.NumPadEnter -> { onDone(); true }
                        Key.Escape -> { undo(); true }
                        else -> false
                    }
                },
            color = MaterialTheme.colorScheme.surface,
            tonalElevation = 6.dp,
            shadowElevation = 8.dp,
        ) {
            Column(Modifier.padding(horizontal = 8.dp, vertical = 4.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    AppearanceStripModel.pages.forEachIndexed { index, item ->
                        TabLabel(item.title, selected = index == pageIndex) { pageIndex = index; rowIndex = 0 }
                    }
                    Box(Modifier.weight(1f))
                    TabLabel(if (publisherStyles) "Publisher: on (P)" else "Publisher: off (P)", selected = false) {
                        onChange(prefs.copy(publisherStyles = !publisherStyles))
                    }
                    TabLabel("All settings", selected = false, onClick = onAllSettings)
                }
                page.rows.forEachIndexed { index, row ->
                    StripRowLine(
                        row = row,
                        prefs = prefs,
                        focused = index == rowIndex,
                        enabled = rowEnabled(page, index),
                        onFocusRow = { rowIndex = index },
                        onPick = { option -> onChange(option.apply(prefs)) },
                        onStep = { delta -> onChange(AppearanceStripModel.step(row, prefs, delta)) },
                    )
                }
                Text(
                    "↑↓ setting   ←→ change   Tab page   Enter keep   Esc undo",
                    fontSize = 10.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(top = 2.dp),
                )
            }
        }
    }
}

private val ALWAYS_ON_ROWS = setOf("Theme", "Size", "Contrast", "Bold")

@Composable
private fun TabLabel(text: String, selected: Boolean, onClick: () -> Unit) {
    Text(
        text,
        fontSize = 12.sp,
        fontWeight = if (selected) FontWeight.Bold else FontWeight.Normal,
        color = if (selected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface,
        modifier = Modifier
            .clickable(onClick = onClick)
            .padding(horizontal = 6.dp, vertical = 4.dp),
    )
}

@Composable
private fun StripRowLine(
    row: StripRow,
    prefs: EpubPreferences,
    focused: Boolean,
    enabled: Boolean,
    onFocusRow: () -> Unit,
    onPick: (StripOption) -> Unit,
    onStep: (Int) -> Unit,
) {
    val selected = row.indexOf(prefs)
    val tint = if (enabled) MaterialTheme.colorScheme.onSurface else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.38f)
    val ring = if (focused) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surface
    Row(
        Modifier
            .fillMaxWidth()
            .height(32.dp)
            .border(2.dp, ring, RoundedCornerShape(6.dp))
            .clickable(onClick = onFocusRow)
            .padding(horizontal = 6.dp)
            .semantics { contentDescription = "${row.label}: ${row.options[selected].label}" },
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(row.label, fontSize = 12.sp, fontWeight = FontWeight.Bold, color = tint, modifier = Modifier.width(78.dp))
        if (row.options.size <= SEGMENTED_MAX) {
            Row(Modifier.weight(1f), horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                row.options.forEachIndexed { index, option ->
                    val on = index == selected
                    Text(
                        option.label,
                        fontSize = 12.sp,
                        color = if (on) MaterialTheme.colorScheme.onPrimary else tint,
                        modifier = Modifier
                            .weight(1f)
                            .background(
                                if (on) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surfaceVariant,
                                RoundedCornerShape(5.dp),
                            )
                            .clickable(enabled = enabled) { onPick(option) }
                            .padding(vertical = 4.dp),
                        maxLines = 1,
                        textAlign = androidx.compose.ui.text.style.TextAlign.Center,
                    )
                }
            }
        } else {
            Row(Modifier.weight(1f), verticalAlignment = Alignment.CenterVertically) {
                Text("◀", fontSize = 14.sp, color = tint, modifier = Modifier.clickable(enabled = enabled) { onStep(-1) }.padding(horizontal = 10.dp))
                Text(
                    row.options[selected].label,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    color = tint,
                    modifier = Modifier.weight(1f),
                    textAlign = androidx.compose.ui.text.style.TextAlign.Center,
                    maxLines = 1,
                )
                Text("▶", fontSize = 14.sp, color = tint, modifier = Modifier.clickable(enabled = enabled) { onStep(1) }.padding(horizontal = 10.dp))
            }
        }
    }
}
