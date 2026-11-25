package org.vaachak.reader.leisure.ui.settings

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsFocusedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.PrimaryTabRow
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.input.key.KeyEventType
import androidx.compose.ui.input.key.isAltPressed
import androidx.compose.ui.input.key.isCtrlPressed
import androidx.compose.ui.input.key.isMetaPressed
import androidx.compose.ui.input.key.onPreviewKeyEvent
import androidx.compose.ui.input.key.utf16CodePoint
import androidx.compose.ui.input.key.type
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import org.vaachak.reader.leisure.ui.testability.Tid
import org.vaachak.reader.leisure.ui.testability.TidScreen

/** The profile-switch dialog, shared by the scrolling Settings and the square-window one. */
@Composable
fun SwitchProfileDialog(viewModel: SettingsViewModel, onDismiss: () -> Unit) {
    var newVaultName by remember { mutableStateOf("") }
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Switch Local Profile") },
        text = {
            Column {
                Text("Enter the name of the offline profile you want to switch to or create.")
                Spacer(Modifier.height(16.dp))
                OutlinedTextField(
                    value = newVaultName,
                    onValueChange = { newVaultName = it },
                    label = { Text("Profile Name") }, // Maestro reads this label
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth(),
                )
            }
        },
        confirmButton = {
            TextButton(onClick = {
                if (newVaultName.isNotBlank()) viewModel.switchVault(newVaultName)
                onDismiss()
            }) { Text("Switch") } // Maestro sees "Switch"
        },
        dismissButton = { TextButton(onClick = onDismiss) { Text("Cancel") } },
    )
}

/**
 * Settings for a square window: four tabs of tiles, no scrolling. Arrow keys move between tiles, Enter opens one, and
 * the letter on a tile opens it from anywhere on the screen. See [SquareSettingsPlan].
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SquareSettingsScreen(
    navController: NavController,
    viewModel: SettingsViewModel,
) {
    val activeVaultId by viewModel.activeVaultId.collectAsState()
    val isMultiUserMode by viewModel.isMultiUserMode.collectAsState()
    val tabs = SquareSettingsPlan.tabs(isMultiUserMode, activeVaultId)
    var selectedTab by rememberSaveable { mutableIntStateOf(0) }
    var showVaultDialog by remember { mutableStateOf(false) }
    val firstTile = remember { FocusRequester() }

    fun open(tile: SquareTile) {
        when (val action = tile.action) {
            is SquareAction.Open -> navController.navigate(action.route)
            SquareAction.ToggleMultiUser -> viewModel.setMultiUserMode(!isMultiUserMode)
            SquareAction.SwitchProfile -> showVaultDialog = true
        }
    }

    val tab = tabs[selectedTab.coerceIn(0, tabs.lastIndex)]
    LaunchedEffect(selectedTab) { runCatching { firstTile.requestFocus() } }

    TidScreen(Tid.Screen.settings) {
        Surface(
            modifier = Modifier
                .fillMaxSize()
                .onPreviewKeyEvent { event ->
                    if (event.type != KeyEventType.KeyDown || event.isCtrlPressed || event.isAltPressed || event.isMetaPressed) {
                        return@onPreviewKeyEvent false
                    }
                    val letter = event.utf16CodePoint.toChar()
                    val tile = SquareSettingsPlan.tileForKey(tabs, letter) ?: return@onPreviewKeyEvent false
                    selectedTab = SquareSettingsPlan.tabIndexOf(tabs, tile)
                    open(tile)
                    true
                },
            color = MaterialTheme.colorScheme.background,
        ) {
            Column(Modifier.fillMaxSize()) {
                PrimaryTabRow(selectedTabIndex = selectedTab.coerceIn(0, tabs.lastIndex)) {
                    tabs.forEachIndexed { index, item ->
                        Tab(
                            selected = selectedTab == index,
                            onClick = { selectedTab = index },
                            modifier = Modifier.semantics { contentDescription = "Settings Tab: ${item.title}" },
                            text = {
                                Text(
                                    item.title,
                                    fontSize = 12.sp,
                                    maxLines = 1,
                                    fontWeight = if (selectedTab == index) FontWeight.Bold else FontWeight.Normal,
                                )
                            },
                        )
                    }
                }
                Column(
                    Modifier.fillMaxSize().padding(10.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                    tab.tiles.chunked(SquareSettingsPlan.COLUMNS).forEachIndexed { rowIndex, row ->
                        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            row.forEachIndexed { colIndex, tile ->
                                Box(Modifier.weight(1f)) {
                                    SquareTileCard(
                                        tile = tile,
                                        onOpen = { open(tile) },
                                        modifier = if (rowIndex == 0 && colIndex == 0) Modifier.focusRequester(firstTile) else Modifier,
                                    )
                                }
                            }
                            repeat(SquareSettingsPlan.COLUMNS - row.size) { Spacer(Modifier.weight(1f)) }
                        }
                    }
                }
            }
        }
    }

    if (showVaultDialog) SwitchProfileDialog(viewModel) { showVaultDialog = false }
}

@Composable
private fun SquareTileCard(tile: SquareTile, onOpen: () -> Unit, modifier: Modifier = Modifier) {
    val interaction = remember { MutableInteractionSource() }
    val focused by interaction.collectIsFocusedAsState()
    val ring = if (focused) BorderStroke(3.dp, MaterialTheme.colorScheme.primary) else BorderStroke(0.dp, MaterialTheme.colorScheme.surfaceVariant)
    Surface(
        modifier = modifier
            .fillMaxWidth()
            .height(92.dp)
            .border(ring, RoundedCornerShape(10.dp))
            .clickable(interactionSource = interaction, indication = null, onClick = onOpen)
            .semantics(mergeDescendants = true) { contentDescription = "${tile.title} setting. ${tile.subtitle}" },
        shape = RoundedCornerShape(10.dp),
        color = MaterialTheme.colorScheme.surfaceVariant,
    ) {
        Box(Modifier.padding(8.dp)) {
            Column(Modifier.fillMaxSize(), verticalArrangement = Arrangement.SpaceBetween) {
                Icon(tile.icon, contentDescription = null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(24.dp))
                Column {
                    Text(tile.title, fontSize = 12.5.sp, fontWeight = FontWeight.Bold, maxLines = 1, overflow = TextOverflow.Ellipsis)
                    Text(
                        tile.subtitle,
                        fontSize = 11.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                    )
                }
            }
            Text(
                tile.hotkey.toString(),
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .border(1.dp, MaterialTheme.colorScheme.onSurfaceVariant, RoundedCornerShape(4.dp))
                    .padding(horizontal = 5.dp),
            )
        }
    }
}
