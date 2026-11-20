package org.sableos.design

import androidx.compose.foundation.BorderStroke
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
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberUpdatedState
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
import androidx.compose.ui.window.DialogProperties
import kotlinx.coroutines.delay

/** The BlackBerry 7 dialog types (UI Guidelines, Dialog boxes). */
enum class SableDialogKind(
    /** The standard indicator that sits left of the message. */
    val indicator: String,
    /** Whether the dialog may carry buttons at all. */
    val allowsButtons: Boolean,
) {
    Alert("!", true),
    Information("i", false),
    Inquiry("?", true),
    Reminder("⏰", true),
    Status("⌛", false),
}

/** A dialog button: a verb label, and the shortcut the keyboard gives it. */
data class SableDialogButton(
    val label: String,
    val onClick: () -> Unit,
)

/** Pure rules from the BlackBerry 7 guidelines, so every app lays dialogs out the same way. */
object SableDialogPolicy {
    const val MAX_BUTTONS = 3
    const val MAX_SIZE_FRACTION = 0.9f
    const val INFORMATION_TIMEOUT_MS = 4000L
    private val DISCOURAGED_LABELS = setOf("yes", "no")

    /** An alert has only OK, information and status have none; inquiries and reminders take up to three. */
    fun buttonsFor(
        kind: SableDialogKind,
        requested: List<SableDialogButton>,
    ): List<SableDialogButton> =
        when {
            !kind.allowsButtons -> emptyList()
            kind == SableDialogKind.Alert -> requested.take(1)
            else -> requested.take(MAX_BUTTONS)
        }

    /** The key that triggers each button: its first letter, unless an earlier button already took it. */
    fun shortcuts(labels: List<String>): List<Char?> {
        val taken = mutableSetOf<Char>()
        return labels.map { label ->
            val c = label.trim().firstOrNull { it.isLetterOrDigit() }?.lowercaseChar()
            if (c != null && taken.add(c)) c else null
        }
    }

    /** Labels the guidelines tell apps to avoid: Yes and No, and punctuation in labels. */
    fun isDiscouragedLabel(label: String): Boolean =
        label.trim().lowercase() in DISCOURAGED_LABELS || label.any { it in ".,;:!?" }
}

/**
 * A BlackBerry 7 dialog box: modal, centred, at most 90% of the screen, indicator beside the message,
 * identical full-width buttons stacked vertically with the confirming one first. Escape (Back) closes it,
 * the first letter of a button triggers it, and Enter presses the highlighted button. Information
 * dialogs close themselves after a short time.
 */
@Composable
fun SableDialog(
    kind: SableDialogKind,
    message: String,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier,
    buttons: List<SableDialogButton> = emptyList(),
) {
    val shown = SableDialogPolicy.buttonsFor(kind, buttons)
    val shortcuts = SableDialogPolicy.shortcuts(shown.map(SableDialogButton::label))
    val dismiss by rememberUpdatedState(onDismiss)
    if (kind == SableDialogKind.Information) {
        LaunchedEffect(message) {
            delay(SableDialogPolicy.INFORMATION_TIMEOUT_MS)
            dismiss()
        }
    }
    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(dismissOnBackPress = kind != SableDialogKind.Status),
    ) {
        Column(
            modifier =
                modifier
                    .fillMaxWidth(SableDialogPolicy.MAX_SIZE_FRACTION)
                    .background(MaterialTheme.colorScheme.surfaceVariant)
                    .border(BorderStroke(1.dp, MaterialTheme.colorScheme.outline))
                    .onPreviewKeyEvent { event ->
                        if (event.type != KeyEventType.KeyDown) return@onPreviewKeyEvent false
                        val index =
                            if (event.key == Key.Enter && shown.isNotEmpty()) {
                                0
                            } else {
                                shortcuts.indexOf(Character.toLowerCase(event.utf16CodePoint).toChar())
                            }
                        if (index >= 0 && index < shown.size) {
                            shown[index].onClick()
                            true
                        } else {
                            false
                        }
                    }.padding(14.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    contentAlignment = Alignment.Center,
                    modifier =
                        Modifier
                            .size(34.dp)
                            .clip(CircleShape)
                            .background(MaterialTheme.colorScheme.primary),
                ) {
                    Text(
                        text = kind.indicator,
                        color = MaterialTheme.colorScheme.onPrimary,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                    )
                }
                Text(
                    text = message,
                    color = MaterialTheme.colorScheme.onSurface,
                    fontSize = 15.sp,
                    modifier = Modifier.weight(1f).padding(start = 12.dp),
                )
            }
            shown.forEachIndexed { index, button ->
                Text(
                    text = button.label,
                    textAlign = TextAlign.Center,
                    color = if (index == 0) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurface,
                    fontSize = 15.sp,
                    modifier =
                        Modifier
                            .fillMaxWidth(0.7f)
                            .align(Alignment.CenterHorizontally)
                            .background(
                                if (index == 0) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surface,
                            ).border(BorderStroke(1.dp, MaterialTheme.colorScheme.outline))
                            .clickable(onClick = button.onClick)
                            .padding(vertical = 9.dp),
                )
            }
        }
    }
}
