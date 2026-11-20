package org.sableos.design

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.selection.toggleable
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.input.key.KeyEventType
import androidx.compose.ui.input.key.key
import androidx.compose.ui.input.key.onPreviewKeyEvent
import androidx.compose.ui.input.key.type
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

/** Text field kinds whose Space key types a helper character (UI Guidelines, Text fields). */
enum class SableTextFieldKind { Email, WebAddress, Text }

/** Pure rules from the BlackBerry 7 guidelines for forms, so apps share one behaviour. */
object SableFormPolicy {
    /** Progress is shown for anything that takes longer than two seconds. */
    const val PROGRESS_AFTER_MS = 2000L

    /**
     * What Space types in the field: an email address gets "@" until it has one, then "."; a web address
     * gets "."; other fields type a plain space (null means leave the key alone).
     */
    fun spaceInsertion(
        kind: SableTextFieldKind,
        current: String,
    ): Char? =
        when (kind) {
            SableTextFieldKind.Email -> if ('@' in current) '.' else '@'
            SableTextFieldKind.WebAddress -> '.'
            SableTextFieldKind.Text -> null
        }

    /** Check box and radio labels use no end punctuation and a sentence-case first letter. */
    fun choiceLabel(label: String): String =
        label.trim().trimEnd('.', ',', ';', ':', '!').replaceFirstChar { it.uppercaseChar() }

    fun percent(fraction: Float): Int = (fraction.coerceIn(0f, 1f) * 100f).toInt()

    fun showProgress(elapsedMs: Long): Boolean = elapsedMs > PROGRESS_AFTER_MS
}

/**
 * Space in an email or web address field types "@" or "." instead of a space. Call [onInsert] with the
 * character; the field appends it at the cursor.
 */
fun Modifier.sableSpaceKey(
    kind: SableTextFieldKind,
    current: () -> String,
    onInsert: (Char) -> Unit,
): Modifier =
    onPreviewKeyEvent { event ->
        if (event.key != Key.Spacebar) return@onPreviewKeyEvent false
        val c = SableFormPolicy.spaceInsertion(kind, current()) ?: return@onPreviewKeyEvent false
        if (event.type == KeyEventType.KeyDown) onInsert(c)
        true
    }

/** A check box row. Label on the right; on Option screens pass [labelOnLeft]. Selecting only toggles. */
@Composable
fun SableCheckRow(
    label: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    modifier: Modifier = Modifier,
    labelOnLeft: Boolean = false,
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier =
            modifier
                .fillMaxWidth()
                .toggleable(value = checked, role = Role.Checkbox, onValueChange = onCheckedChange)
                .padding(vertical = 4.dp),
    ) {
        if (labelOnLeft) Label(SableFormPolicy.choiceLabel(label), Modifier.weight(1f))
        Checkbox(checked = checked, onCheckedChange = null)
        if (!labelOnLeft) Label(SableFormPolicy.choiceLabel(label), Modifier.weight(1f).padding(start = 8.dp))
    }
}

/** A radio button row, label on the right. Selecting only selects; it never opens another screen. */
@Composable
fun SableRadioRow(
    label: String,
    selected: Boolean,
    onSelect: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier =
            modifier
                .fillMaxWidth()
                .selectable(selected = selected, role = Role.RadioButton, onClick = onSelect)
                .padding(vertical = 4.dp),
    ) {
        RadioButton(selected = selected, onClick = null)
        Label(SableFormPolicy.choiceLabel(label), Modifier.weight(1f).padding(start = 8.dp))
    }
}

@Composable
private fun Label(
    text: String,
    modifier: Modifier,
) = Text(text = text, color = MaterialTheme.colorScheme.onSurface, fontSize = 15.sp, modifier = modifier)

/** An activity indicator for work of unknown length, with a short sentence-case description. */
@Composable
fun SableActivityIndicator(
    text: String,
    modifier: Modifier = Modifier,
) {
    Row(verticalAlignment = Alignment.CenterVertically, modifier = modifier.padding(8.dp)) {
        CircularProgressIndicator(modifier = Modifier.size(22.dp), strokeWidth = 2.dp)
        Text(text = text, fontSize = 14.sp, color = MaterialTheme.colorScheme.onSurface, modifier = Modifier.padding(start = 10.dp))
    }
}

/** A progress indicator for work of known length: a label and a bar that fills left to right with the percentage in it. */
@Composable
fun SableProgressIndicator(
    label: String,
    fraction: Float,
    modifier: Modifier = Modifier,
) {
    val percent = SableFormPolicy.percent(fraction)
    Column(verticalArrangement = Arrangement.spacedBy(4.dp), modifier = modifier.fillMaxWidth().padding(8.dp)) {
        Text(text = label, fontSize = 14.sp, color = MaterialTheme.colorScheme.onSurface)
        Box(
            contentAlignment = Alignment.Center,
            modifier =
                Modifier
                    .fillMaxWidth()
                    .height(20.dp)
                    .background(MaterialTheme.colorScheme.surfaceVariant),
        ) {
            Box(
                modifier =
                    Modifier
                        .fillMaxWidth(percent / 100f)
                        .height(20.dp)
                        .background(MaterialTheme.colorScheme.primary)
                        .align(Alignment.CenterStart),
            )
            Text(text = "$percent%", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurface)
        }
    }
}
