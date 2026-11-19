// Patch history: 0061, 0085, 0166, 0206 (see docs/PATCH_HISTORY.md)
package org.sableos.media

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import org.sableos.design.SableActionButton
import org.sableos.design.SableSpacing
import org.sableos.design.SableValuePanel
import org.sableos.media.MediaText.t

/** Whether the FM destination is offered in the top navigation (the FM bridge is installed). */
internal enum class FmStationsTab(
    val labelKey: String,
) {
    All("fm_tab_all"),
    Favourites("fm_tab_favourites"),
    Presets("fm_tab_presets"),
}

/** Whether the FM screen shows the Stations list (the bar's Stations) instead of the dial, and which tab. */
internal object FmViewState {
    var showStations by mutableStateOf(false)
    var tab by mutableStateOf(FmStationsTab.All)
}

internal val LocalFmAvailable = staticCompositionLocalOf { false }

private val FM_KEY_HINT: String get() = t("space_on_off_j_k")

internal data class FmScreenActions(
    val onPower: () -> Unit,
    val onSeek: (up: Boolean) -> Unit,
    val onStep: (up: Boolean) -> Unit,
    val onTune: (Int) -> Unit,
    val onToggleFavorite: (Int) -> Unit,
    val onToggleSpeaker: () -> Unit,
    val onToggleMute: () -> Unit,
    val onScan: () -> Unit,
    val onToggleRecording: () -> Unit = {},
    /** Gives a station a name of the person's own; a blank name removes it. */
    val onRename: (Int, String) -> Unit = { _, _ -> },
)

/** Sable Media FM mode (SABLE_MEDIA_FM_RADIO_MODE): tuner, presets and the stations the last scan found. */
@Composable
internal fun FmScreen(
    fm: FmUiState,
    favorites: Set<Int>,
    actions: FmScreenActions,
    names: Map<Int, String> = emptyMap(),
) {
    Column(verticalArrangement = Arrangement.spacedBy(SableSpacing.Lg)) {
        if (!fm.available) {
            SableValuePanel(
                label = t("fm_radio"),
                value = t("not_available"),
                supportingText = t("this_device_has_no_fm"),
            )
        } else {
            if (FmViewState.showStations) {
                FmStationsScreen(fm, favorites, names, actions)
            } else {
                FmTunerPanel(fm, names)
                Text(
                    text = FM_KEY_HINT,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
        }
    }
}

@Composable
private fun FmTunerPanel(
    fm: FmUiState,
    names: Map<Int, String>,
) {
    val tuned = FmBand.isValid(fm.station)
    val name =
        names[fm.station] ?: fm.programService.ifBlank {
            fm.stations
                .firstOrNull { it.station == fm.station }
                ?.name
                .orEmpty()
        }
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = MaterialTheme.shapes.medium,
        color = MaterialTheme.colorScheme.surface,
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
    ) {
        Column(
            modifier = Modifier.padding(SableSpacing.Md),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(SableSpacing.Xs),
        ) {
            if (fm.recording) {
                Text(
                    text = "● " + t("fm_rec") + " " + FmBand.clock(fm.recordMs),
                    style = MaterialTheme.typography.labelLarge,
                    color = FmRecordRed,
                    fontWeight = FontWeight.Bold,
                )
            }
            Row(verticalAlignment = Alignment.Bottom) {
                Text(
                    text = if (tuned) FmBand.format(fm.station) else "--.-",
                    style = MaterialTheme.typography.displayMedium,
                    color = MaterialTheme.colorScheme.onSurface,
                    fontWeight = FontWeight.Bold,
                    maxLines = 1,
                )
                Text(
                    text = " MHz",
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(bottom = SableSpacing.Sm),
                )
            }
            FmDial(station = fm.station.takeIf { tuned })
            if (name.isNotBlank()) {
                Text(
                    text = name,
                    style = MaterialTheme.typography.titleMedium,
                    color = MediaAccent,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
            }
            if (fm.radioText.isNotBlank()) {
                Text(
                    text = fm.radioText,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                )
            }
            Text(
                text = FmBand.status(fm),
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
    }
    if (fm.connected && !fm.antenna) {
        Text(
            text =
                t("fm_uses_wired_headphones_as"),
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.error,
        )
    }
}

internal val FmRecordRed = Color(0xFFE5484D)
private val FmDialHeight = 36.dp

/** The tuning dial: a tick every 0.1 MHz, a taller one every MHz, and a needle on the tuned station. */
@Composable
private fun FmDial(station: Int?) {
    val tick = MaterialTheme.colorScheme.outline
    val needle = MediaAccent
    Canvas(Modifier.fillMaxWidth().height(FmDialHeight)) {
        val low = FmBridgeProtocol.LOWEST_STATION
        val high = FmBridgeProtocol.HIGHEST_STATION
        val span = (high - low).toFloat()
        for (value in low..high) {
            val x = (value - low) / span * size.width
            val major = value % 10 == 0
            val mid = value % 5 == 0
            val length = if (major) size.height * 0.7f else if (mid) size.height * 0.5f else size.height * 0.3f
            drawLine(tick, Offset(x, size.height), Offset(x, size.height - length), strokeWidth = if (major) 2f else 1f)
        }
        if (station != null) {
            val x = (station - low) / span * size.width
            drawLine(needle, Offset(x, 0f), Offset(x, size.height), strokeWidth = 4f)
        }
    }
}

/** The Stations screen of the approved board: All, Favourites and Presets tabs over one list of stations. */
@Composable
private fun FmStationsScreen(
    fm: FmUiState,
    favorites: Set<Int>,
    names: Map<Int, String>,
    actions: FmScreenActions,
) {
    val tab = FmViewState.tab
    val presets = FmPresets.ordered(favorites)
    val rows =
        when (tab) {
            FmStationsTab.All -> FmPresets.list(favorites, fm.stations)
            FmStationsTab.Favourites ->
                FmPresets.list(favorites, fm.stations).filter { it.station in favorites }
            FmStationsTab.Presets ->
                presets.take(FmPresets.MAX_KEYED).map { station ->
                    fm.stations.firstOrNull { it.station == station } ?: FmStation(station, "")
                }
        }
    Column {
        Row(Modifier.fillMaxWidth().height(30.dp), verticalAlignment = Alignment.CenterVertically) {
            FmStationsTab.entries.forEach { entry ->
                val chosen = entry == tab
                Text(
                    text = t(entry.labelKey),
                    fontSize = 13.sp,
                    fontWeight = if (chosen) FontWeight.SemiBold else FontWeight.Normal,
                    color = if (chosen) MediaAccent else MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier =
                        Modifier
                            .clickable(role = Role.Tab) { FmViewState.tab = entry }
                            .padding(horizontal = 8.dp, vertical = 6.dp),
                )
            }
        }
        if (rows.isEmpty()) {
            Text(
                text = t("fm_none_in_tab"),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(vertical = 12.dp),
            )
        }
        rows.forEach { station ->
            val index = presets.indexOf(station.station)
            FmStationLine(
                station = station,
                customName = names[station.station].orEmpty(),
                preset = if (index in 0 until FmPresets.MAX_KEYED) index + 1 else null,
                starred = station.station in favorites,
                current = station.station == fm.station && fm.powered,
                onTune = { actions.onTune(station.station) },
                onToggleFavorite = { actions.onToggleFavorite(station.station) },
                onRename = { name -> actions.onRename(station.station, name) },
            )
        }
    }
}

/** A station row: its frequency, its name and what it is, the blue star; the tuned one is filled blue. */
@Composable
private fun FmStationLine(
    station: FmStation,
    customName: String,
    preset: Int?,
    starred: Boolean,
    current: Boolean,
    onTune: () -> Unit,
    onToggleFavorite: () -> Unit,
    onRename: (String) -> Unit,
) {
    var naming by remember(station.station) { mutableStateOf(false) }
    val title = customName.ifBlank { station.name.ifBlank { "${FmBand.format(station.station)} MHz" } }
    val detail =
        listOfNotNull(
            "FM".takeIf { customName.isNotBlank() || station.name.isNotBlank() },
            preset?.let { "Alt+$it" },
        ).joinToString(" · ")
    val ink = if (current) Color.White else MaterialTheme.colorScheme.onSurface
    val muted = if (current) Color.White else MaterialTheme.colorScheme.onSurfaceVariant
    Column {
        Row(
            modifier =
                Modifier
                    .fillMaxWidth()
                    .background(if (current) MediaAccent else Color.Transparent)
                    .combinedClickable(
                        role = Role.Button,
                        onClick = onTune,
                        onLongClick = { if (starred) naming = true },
                    ).padding(horizontal = 12.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                text = FmBand.format(station.station),
                fontSize = 22.sp,
                color = ink,
                modifier = Modifier.width(64.dp),
                maxLines = 1,
            )
            Column(Modifier.weight(1f)) {
                Text(
                    text = title,
                    fontSize = 17.sp,
                    fontWeight = FontWeight.Bold,
                    color = ink,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
                if (detail.isNotBlank()) {
                    Text(text = detail, fontSize = 13.sp, color = muted, maxLines = 1, overflow = TextOverflow.Ellipsis)
                }
            }
            Icon(
                imageVector = if (starred) MediaIcons.StarFilled else MediaIcons.Star,
                contentDescription = if (starred) t("remove_star") else t("star_2"),
                tint = if (current) Color.White else MediaAccent,
                modifier =
                    Modifier
                        .size(32.dp)
                        .clickable(role = Role.Button, onClick = onToggleFavorite)
                        .padding(4.dp),
            )
        }
        if (naming) {
            FmNameEditor(
                initial = customName.ifBlank { station.name },
                onSave = { name ->
                    naming = false
                    onRename(name)
                },
                onCancel = { naming = false },
            )
        }
    }
}

/** Names a starred station (owner, 2026-11-13): a name of the person's own, or blank to clear it. */
@Composable
private fun FmNameEditor(
    initial: String,
    onSave: (String) -> Unit,
    onCancel: () -> Unit,
) {
    var name by remember { mutableStateOf(initial) }
    Column(
        modifier = Modifier.padding(horizontal = SableSpacing.Md, vertical = SableSpacing.Sm),
        verticalArrangement = Arrangement.spacedBy(SableSpacing.Sm),
    ) {
        OutlinedTextField(
            value = name,
            onValueChange = { name = it.take(FmPresets.MAX_NAME) },
            label = { Text(t("station_name")) },
            singleLine = true,
            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
            keyboardActions = KeyboardActions(onDone = { onSave(name) }),
            modifier = Modifier.fillMaxWidth(),
        )
        Row(horizontalArrangement = Arrangement.spacedBy(SableSpacing.Sm)) {
            SableActionButton(text = t("hub_save"), modifier = Modifier.weight(1f), onClick = { onSave(name) })
            SableActionButton(
                text = t("fm_clear_name"),
                primary = false,
                modifier = Modifier.weight(1f),
                onClick = { onSave("") },
            )
            SableActionButton(text = t("cancel"), primary = false, modifier = Modifier.weight(1f), onClick = onCancel)
        }
    }
}
