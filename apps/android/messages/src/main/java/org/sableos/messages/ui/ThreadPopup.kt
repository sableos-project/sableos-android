package org.sableos.messages.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import org.sableos.design.sableFocusRing
import org.sableos.messages.core.ThreadSummary

private val CELL_WIDTH = 92.dp
private val CELL_HEIGHT = 72.dp

/**
 * The BlackBerry 7 pop-up menu of the approved Messages board: a 3 x 3 grid opened by a trackpad hold on a
 * conversation, Open in the centre. The cells are the actions this SMS app has: Reply, Call, New, Delete, Open,
 * Mark read and Close. Back closes it.
 */
@Composable
@Suppress("FunctionNaming")
internal fun ThreadPopup(
    thread: ThreadSummary,
    actions: MessagesActions,
    onClose: () -> Unit,
) {
    val open = remember { FocusRequester() }
    LaunchedEffect(thread.threadId) { runCatching { open.requestFocus() } }
    fun then(action: () -> Unit): () -> Unit =
        {
            onClose()
            action()
        }
    val cells =
        listOf(
            PopupCell("Reply") { actions.openThread(thread) },
            PopupCell("Call") { actions.callThread(thread) },
            PopupCell("New") { actions.newMessage() },
            PopupCell("Delete") { actions.deleteThread(thread) },
            PopupCell("Open") { actions.openThread(thread) },
            PopupCell("Mark read") { actions.markThreadRead(thread) },
            null,
            null,
            PopupCell("Close") {},
        )
    Dialog(onDismissRequest = onClose) {
        Column(
            modifier =
                Modifier
                    .background(MaterialTheme.colorScheme.surface)
                    .border(1.dp, MaterialTheme.colorScheme.outlineVariant)
                    .padding(8.dp),
        ) {
            cells.chunked(3).forEachIndexed { rowIndex, row ->
                Row {
                    row.forEachIndexed { columnIndex, cell ->
                        val centre = rowIndex == 1 && columnIndex == 1
                        PopupCellView(cell, centre, if (centre) Modifier.focusRequester(open) else Modifier) {
                            cell?.let { then(it.action)() }
                        }
                    }
                }
            }
        }
    }
}

private class PopupCell(
    val label: String,
    val action: () -> Unit,
)

@Composable
@Suppress("FunctionNaming")
private fun PopupCellView(
    cell: PopupCell?,
    centre: Boolean,
    modifier: Modifier,
    onClick: () -> Unit,
) {
    val accent = MaterialTheme.colorScheme.primary
    Box(
        modifier =
            modifier
                .width(CELL_WIDTH)
                .height(CELL_HEIGHT)
                .then(if (centre) Modifier.background(accent.copy(alpha = 0.35f)) else Modifier)
                .then(if (cell != null) Modifier.sableFocusRing().clickable(onClick = onClick) else Modifier)
                .padding(4.dp),
        contentAlignment = Alignment.Center,
    ) {
        if (cell != null) {
            Text(
                cell.label,
                fontSize = 12.sp,
                color = if (cell.label == "Delete") MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.onSurface,
                textAlign = TextAlign.Center,
            )
        }
    }
}
