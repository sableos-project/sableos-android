package org.sableos.reader.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.input.key.KeyEventType
import androidx.compose.ui.input.key.key
import androidx.compose.ui.input.key.onPreviewKeyEvent
import androidx.compose.ui.input.key.type
import androidx.compose.ui.input.key.utf16CodePoint
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog

/**
 * A BlackBerry 7 inquiry dialog: a "?" indicator beside the question, the confirming button first,
 * a Cancel button below it, both stacked and the same width. Back closes it, Enter presses the first
 * button and the first letter of a button presses it (UI Guidelines, Dialog boxes). This module has its
 * own design code, so this is a small copy of the design library's SableDialog for confirm prompts.
 */
@Composable
fun Bb7ConfirmDialog(
    message: String,
    confirmLabel: String,
    onConfirm: () -> Unit,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier,
    cancelLabel: String = "Cancel",
) {
    val confirmKey = confirmLabel.firstOrNull()?.lowercaseChar()
    val cancelKey = cancelLabel.firstOrNull()?.lowercaseChar()?.takeIf { it != confirmKey }
    Dialog(onDismissRequest = onDismiss) {
        Column(
            modifier =
                modifier
                    .fillMaxWidth(0.9f)
                    .background(MaterialTheme.colorScheme.surfaceVariant)
                    .border(1.dp, MaterialTheme.colorScheme.outline)
                    .onPreviewKeyEvent { event ->
                        if (event.type != KeyEventType.KeyDown) return@onPreviewKeyEvent false
                        val typed = Character.toLowerCase(event.utf16CodePoint).toChar()
                        when {
                            event.key == Key.Enter || typed == confirmKey -> {
                                onConfirm()
                                true
                            }
                            typed == cancelKey -> {
                                onDismiss()
                                true
                            }
                            else -> false
                        }
                    }.padding(14.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier.size(34.dp).clip(CircleShape).background(MaterialTheme.colorScheme.primary),
                ) {
                    Text("?", color = MaterialTheme.colorScheme.onPrimary, fontSize = 18.sp, fontWeight = FontWeight.Bold)
                }
                Text(
                    text = message,
                    color = MaterialTheme.colorScheme.onSurface,
                    fontSize = 15.sp,
                    modifier = Modifier.weight(1f).padding(start = 12.dp),
                )
            }
            Bb7DialogButton(confirmLabel, primary = true, onClick = onConfirm)
            Bb7DialogButton(cancelLabel, primary = false, onClick = onDismiss)
        }
    }
}

@Composable
private fun androidx.compose.foundation.layout.ColumnScope.Bb7DialogButton(
    label: String,
    primary: Boolean,
    onClick: () -> Unit,
) {
    Text(
        text = label,
        textAlign = TextAlign.Center,
        color = if (primary) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurface,
        fontSize = 15.sp,
        modifier =
            Modifier
                .fillMaxWidth(0.7f)
                .align(Alignment.CenterHorizontally)
                .background(if (primary) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surface)
                .border(1.dp, MaterialTheme.colorScheme.outline)
                .clickable(onClick = onClick)
                .padding(vertical = 9.dp),
    )
}
