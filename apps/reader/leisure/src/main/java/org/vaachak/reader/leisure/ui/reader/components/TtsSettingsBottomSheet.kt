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

@file:OptIn(org.readium.r2.shared.ExperimentalReadiumApi::class, androidx.compose.material3.ExperimentalMaterial3Api::class)

package org.vaachak.reader.leisure.ui.reader.components


import android.content.Intent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuAnchorType
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import org.readium.navigator.media.tts.android.AndroidTtsEngine
import org.vaachak.reader.core.domain.model.TtsSettings
import org.vaachak.reader.leisure.ui.reader.ReaderViewModel
import org.vaachak.reader.leisure.ui.settings.TtsOptionChip
import org.vaachak.reader.leisure.ui.testability.Tid
import org.vaachak.reader.leisure.ui.testability.tid
import java.util.Locale


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TtsSettingsBottomSheet(
    isVisible: Boolean,
    onDismiss: () -> Unit,
    ttsSettings: TtsSettings,
    onSpeedChange: (Float) -> Unit,
    onPitchChange: (Float) -> Unit,
    onStyleChange: (String) -> Unit,
    onAutoPageTurnChange: (Boolean) -> Unit,
    viewModel: ReaderViewModel
) {
    val containerColor = MaterialTheme.colorScheme.surface
    val contentColor = MaterialTheme.colorScheme.onSurface

    // 1. INVISIBLE SCRIM (Prevents the screen from dimming)
    if (isVisible) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .clickable(
                    interactionSource = remember { MutableInteractionSource() },
                    indication = null,
                    onClick = onDismiss
                )
        )
    }

    // 2. THE BOTTOM SHEET CONTENT
    AnimatedVisibility(
        visible = isVisible,
        enter = slideInVertically(initialOffsetY = { fullHeight -> fullHeight }),
        exit = slideOutVertically(targetOffsetY = { fullHeight -> fullHeight }),
        modifier = Modifier.fillMaxSize()
    ) {
        Box(contentAlignment = Alignment.BottomCenter) {
            Column(
                modifier = Modifier
                    .tid(Tid.Screen.ttsSettings)
                    .fillMaxWidth()
                    .background(containerColor, RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp))
                    // Block clicks from passing through the sheet to the scrim behind it
                    .clickable(interactionSource = remember { MutableInteractionSource() }, indication = null) {}
                    .padding(24.dp, bottom = 24.dp)
                    .navigationBarsPadding(),
                verticalArrangement = Arrangement.spacedBy(20.dp) // Tighter spacing
            ) {

                // Custom Drag Handle
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 4.dp, bottom = 8.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Box(
                        modifier = Modifier
                            .size(width = 32.dp, height = 4.dp)
                            .background(contentColor.copy(alpha = 0.4f), RoundedCornerShape(2.dp))
                    )
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("TTS Settings", style = MaterialTheme.typography.titleMedium, color = contentColor, fontWeight = FontWeight.Bold)
                    IconButton(onClick = onDismiss, modifier = Modifier.tid(Tid.Tts.settingsClose)) {
                        Icon(Icons.Default.Close, contentDescription = "Close TTS Settings", tint = contentColor)
                    }
                }

                // --- ROW 1: SPEED & PITCH (Side-by-Side) ---
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    CompactStepper(
                        title = "Speed",
                        value = ttsSettings.defaultSpeed,
                        onValueChange = onSpeedChange,
                        format = { "${"%.1f".format(it)}x" },
                        modifier = Modifier.weight(1f)
                    )
                    CompactStepper(
                        title = "Pitch",
                        value = ttsSettings.pitch,
                        onValueChange = onPitchChange,
                        format = { "${"%.1f".format(it)}x" },
                        modifier = Modifier.weight(1f)
                    )
                }

                HorizontalDivider(color = contentColor.copy(alpha = 0.2f))

                // --- ROW 2: LANGUAGE & VOICE come from Android's Text-to-speech settings ---
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        "Language and voice follow Android's Text-to-speech settings.",
                        style = MaterialTheme.typography.bodySmall,
                        color = contentColor.copy(alpha = 0.8f),
                        modifier = Modifier.weight(1f)
                    )
                    val context = LocalContext.current
                    OutlinedButton(
                        onClick = {
                            context.startActivity(Intent("com.android.settings.TTS_SETTINGS").apply {
                                flags = Intent.FLAG_ACTIVITY_NEW_TASK
                            })
                        },
                        modifier = Modifier.semantics { contentDescription = "Open Android text-to-speech settings" }
                    ) {
                        Text("Change", color = contentColor)
                    }
                }

                HorizontalDivider(color = contentColor.copy(alpha = 0.2f))

                // --- ROW 3: STYLE & AUTO-TURN (Side-by-Side) ---
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                    Column {
                        Text("Highlight Style", style = MaterialTheme.typography.labelSmall, color = contentColor.copy(alpha = 0.7f))
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            TtsOptionChip("Underline", ttsSettings.visualStyle == "underline") { onStyleChange("underline") }
                            TtsOptionChip("Block", ttsSettings.visualStyle == "highlight") { onStyleChange("highlight") }
                        }
                    }

                    Column(horizontalAlignment = Alignment.End) {
                        Text("Auto-Turn Pages", style = MaterialTheme.typography.labelSmall, color = contentColor.copy(alpha = 0.7f))
                        Switch(
                            checked = ttsSettings.isAutoPageTurnEnabled,
                            onCheckedChange = onAutoPageTurnChange,
                            modifier = Modifier.semantics(mergeDescendants = true) {
                                contentDescription = "Auto-Turn Pages, ${if (ttsSettings.isAutoPageTurnEnabled) "On" else "Off"}"
                            }
                        )
                    }
                }
            }
        }
    }
}

// Compact Stepper Helper for Row 1
@Composable
fun CompactStepper(title: String, value: Float, onValueChange: (Float) -> Unit, format: (Float) -> String, modifier: Modifier = Modifier) {
    Column(modifier = modifier) {
        Text(title, style = MaterialTheme.typography.labelSmall)
        Row(verticalAlignment = Alignment.CenterVertically) {
            IconButton(onClick = { onValueChange(value - 0.1f) }) {
                Icon(Icons.Default.Remove, contentDescription = "Decrease $title")
            }
            Text(format(value), style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
            IconButton(onClick = { onValueChange(value + 0.1f) }) {
                Icon(Icons.Default.Add, contentDescription = "Increase $title")
            }
        }
    }
}
