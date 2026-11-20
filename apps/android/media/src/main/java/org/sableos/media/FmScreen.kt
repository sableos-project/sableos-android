// Patch history: 0061, 0085, 0166, 0206 (see docs/PATCH_HISTORY.md)
package org.sableos.media

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
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
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.drawText
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import org.sableos.design.SableActionButton
import org.sableos.design.SableSpacing
import org.sableos.design.SableValuePanel
import org.sableos.design.sableFocusRing
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

internal val FM_KEY_HINT: String get() = t("hint_fm_dial")

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
    Column(verticalArrangement = Arrangement.spacedBy(if (FmViewState.showStations) SableSpacing.Lg else SableSpacing.Sm)) {
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
                FmTunerPanel(fm, favorites, names, actions.onToggleFavorite)
            }
        }
    }
}

@Composable
private fun FmTunerPanel(
    fm: FmUiState,
    favorites: Set<Int>,
    names: Map<Int, String>,
    onToggleFavorite: (Int) -> Unit,
) {
    val tuned = FmBand.isValid(fm.station)
    val name =
        names[fm.station] ?: fm.programService.ifBlank {
            fm.stations
                .firstOrNull { it.station == fm.station }
                ?.name
                .orEmpty()
        }
    // The approved FM board: a big frequency with MHz under it, a dial that scrolls round the tuned station, then
    // the station and what is playing.
    Column(Modifier.fillMaxWidth(), horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            text = if (tuned) FmBand.format(fm.station) else "--.-",
            fontSize = 50.sp,
            lineHeight = 54.sp,
            fontWeight = FontWeight.Light,
            color = MaterialTheme.colorScheme.onSurface,
            maxLines = 1,
        )
        Text("MHz", fontSize = 11.sp, color = MediaAccent, letterSpacing = 2.sp)
        if (fm.recording) {
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                Box(Modifier.size(8.dp).background(FmRecordRed, CircleShape))
                Text(
                    text = t("fm_rec") + " " + FmBand.clock(fm.recordMs),
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface,
                )
            }
        }
    }
    FmDial(station = fm.station.takeIf { tuned })
    HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant, modifier = Modifier.padding(horizontal = 12.dp))
    Row(
        modifier = Modifier.fillMaxWidth().padding(horizontal = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(10.dp),
    ) {
        Column(Modifier.weight(1f)) {
            Text(
                text = if (tuned) t("live_fm") + " \u00B7 " + FmBand.status(fm) else FmBand.status(fm),
                fontSize = 11.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
            Text(
                text = name.ifBlank { if (tuned) FmBand.format(fm.station) + " MHz" else "" },
                fontSize = 21.sp,
                lineHeight = 26.sp,
                fontWeight = FontWeight.Bold,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
        }
        if (tuned) {
            val starred = fm.station in favorites
            Icon(
                imageVector = if (starred) MediaIcons.StarFilled else MediaIcons.Star,
                contentDescription = t("fm_favourites"),
                tint = if (starred) MediaAccent else MaterialTheme.colorScheme.onSurfaceVariant,
                modifier =
                    Modifier
                        .sableFocusRing()
                        .clickable(role = Role.Button) { onToggleFavorite(fm.station) }
                        .padding(6.dp)
                        .size(26.dp),
            )
        }
    }
    if (fm.radioText.isNotBlank()) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(horizontal = 12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(10.dp),
        ) {
            Box(
                modifier =
                    Modifier
                        .size(64.dp)
                        .background(MaterialTheme.colorScheme.surface)
                        .border(1.dp, MaterialTheme.colorScheme.outlineVariant),
                contentAlignment = Alignment.Center,
            ) {
                Icon(
                    MediaIcons.Dial,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.size(30.dp),
                )
            }
            Text(
                text = fm.radioText,
                fontSize = 15.sp,
                lineHeight = 19.sp,
                fontWeight = FontWeight.Bold,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.weight(1f),
            )
        }
    }
    if (fm.connected && !fm.antenna) {
        Text(
            text = t("fm_uses_wired_headphones_as"),
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.error,
            modifier = Modifier.padding(horizontal = 12.dp),
        )
    }
}

internal val FmRecordRed = Color(0xFFE5484D)
private val FmDialHeight = 30.dp
private const val FM_DIAL_HALF_SPAN = 25 // 2.5 MHz either side of the needle, in 100 kHz steps

/**
 * The tuning dial of the board: it scrolls round the tuned station, a tick every 0.1 MHz and a taller one every
 * MHz, a label at each whole MHz, and the needle fixed in the middle.
 */
@Composable
private fun FmDial(station: Int?) {
    val tick = MaterialTheme.colorScheme.outline
    val major = MaterialTheme.colorScheme.onSurfaceVariant
    val needle = MediaAccent
    val ink = MaterialTheme.colorScheme.onSurfaceVariant
    val measurer = rememberTextMeasurer()
    val centre = station ?: ((FmBridgeProtocol.LOWEST_STATION + FmBridgeProtocol.HIGHEST_STATION) / 2)
    Canvas(Modifier.fillMaxWidth().height(FmDialHeight + 14.dp)) {
        val from = centre - FM_DIAL_HALF_SPAN
        val to = centre + FM_DIAL_HALF_SPAN
        val span = (to - from).toFloat()
        val tickHeight = FmDialHeight.toPx()
        for (value in from..to) {
            if (value < FmBridgeProtocol.LOWEST_STATION || value > FmBridgeProtocol.HIGHEST_STATION) continue
            val x = (value - from) / span * size.width
            val whole = value % 10 == 0
            val half = value % 5 == 0
            val length = if (whole) tickHeight * 0.6f else if (half) tickHeight * 0.42f else tickHeight * 0.26f
            drawLine(if (whole) major else tick, Offset(x, tickHeight), Offset(x, tickHeight - length), strokeWidth = if (whole) 3f else 1.5f)
            if (whole) {
                val label = measurer.measure((value / 10).toString(), TextStyle(fontSize = 10.sp, color = ink))
                drawText(label, topLeft = Offset(x - label.size.width / 2f, tickHeight + 2f))
            }
        }
        if (station != null) drawLine(needle, Offset(size.width / 2f, 0f), Offset(size.width / 2f, tickHeight), strokeWidth = 6f)
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
