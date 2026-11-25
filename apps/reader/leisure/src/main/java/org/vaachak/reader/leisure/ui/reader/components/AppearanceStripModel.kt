@file:OptIn(org.readium.r2.shared.ExperimentalReadiumApi::class)

package org.vaachak.reader.leisure.ui.reader.components

import kotlin.math.abs
import org.readium.r2.navigator.epub.EpubPreferences
import org.readium.r2.navigator.preferences.FontFamily
import org.readium.r2.navigator.preferences.TextAlign
import org.readium.r2.navigator.preferences.Theme

/** One choice of a row: its label and how it changes the reading preferences. */
class StripOption(val label: String, val apply: (EpubPreferences) -> EpubPreferences)

/** One line of the appearance strip: a name, its choices, and which choice the preferences currently match. */
class StripRow(val label: String, val options: List<StripOption>, val indexOf: (EpubPreferences) -> Int)

class StripPage(val title: String, val rows: List<StripRow>)

/**
 * What the strip along the bottom of a square window offers: the Display and Layout settings of the full Reader
 * Settings, as presets that can be stepped with the arrow keys while the book stays visible. The exact sliders stay
 * in the full settings. Numeric rows pick the preset nearest to the saved value, so a value set with a slider still
 * shows a sensible choice.
 */
object AppearanceStripModel {
    private val fonts: List<Pair<String, FontFamily?>> =
        listOf(
            "Original" to null,
            "Sans" to FontFamily.SANS_SERIF,
            "Serif" to FontFamily.SERIF,
            "Mono" to FontFamily.MONOSPACE,
            "Cursive" to FontFamily.CURSIVE,
            "Dyslexic" to FontFamily.OPEN_DYSLEXIC,
            "Accessible" to FontFamily.ACCESSIBLE_DFA,
            "Writer" to FontFamily.IA_WRITER_DUOSPACE,
        )

    private val themeRow =
        StripRow(
            label = "Theme",
            options =
                listOf(
                    StripOption("SableOS") { it.copy(theme = Theme.LIGHT) },
                    StripOption("Sepia") { it.copy(theme = Theme.SEPIA) },
                ),
            indexOf = { if (it.theme == Theme.SEPIA) 1 else 0 },
        )

    private val fontRow =
        StripRow(
            label = "Font",
            options = fonts.map { (name, family) -> StripOption(name) { it.copy(fontFamily = family) } },
            indexOf = { prefs -> fonts.indexOfFirst { it.second?.name == prefs.fontFamily?.name }.coerceAtLeast(0) },
        )

    private val alignRow =
        StripRow(
            label = "Align",
            options =
                listOf(
                    StripOption("Auto") { it.copy(textAlign = TextAlign.START) },
                    StripOption("Left") { it.copy(textAlign = TextAlign.LEFT) },
                    StripOption("Justify") { it.copy(textAlign = TextAlign.JUSTIFY) },
                ),
            indexOf = {
                when (it.textAlign) {
                    TextAlign.LEFT -> 1
                    TextAlign.JUSTIFY -> 2
                    else -> 0
                }
            },
        )

    private fun numericRow(
        label: String,
        presets: List<Pair<String, Double>>,
        default: Double,
        read: (EpubPreferences) -> Double?,
        write: (EpubPreferences, Double) -> EpubPreferences,
    ): StripRow =
        StripRow(
            label = label,
            options = presets.map { (name, value) -> StripOption(name) { write(it, value) } },
            indexOf = { prefs ->
                val current = read(prefs) ?: default
                presets.indices.minByOrNull { abs(presets[it].second - current) } ?: 0
            },
        )

    private val sizeRow =
        numericRow(
            "Size",
            listOf(
                "80%" to 0.8, "90%" to 0.9, "100%" to 1.0, "110%" to 1.1, "125%" to 1.25,
                "150%" to 1.5, "175%" to 1.75, "200%" to 2.0, "250%" to 2.5,
            ),
            1.0,
            { it.fontSize },
            { prefs, value -> prefs.copy(fontSize = value) },
        )

    private val lineRow =
        numericRow(
            "Line height",
            listOf("Tight" to 1.0, "Normal" to 1.2, "Loose" to 1.5, "Looser" to 1.8),
            1.2,
            { it.lineHeight },
            { prefs, value -> prefs.copy(lineHeight = value) },
        )

    private val paragraphRow =
        numericRow(
            "Paragraph",
            listOf("None" to 0.0, "Small" to 0.5, "Large" to 1.0, "Larger" to 1.5),
            0.5,
            { it.paragraphSpacing },
            { prefs, value -> prefs.copy(paragraphSpacing = value) },
        )

    private val lettersRow =
        numericRow(
            "Letters",
            listOf("Tight" to 0.0, "Normal" to 0.1, "Wide" to 0.2, "Wider" to 0.3),
            0.1,
            { it.letterSpacing },
            { prefs, value -> prefs.copy(letterSpacing = value) },
        )

    private val marginsRow =
        numericRow(
            "Margins",
            listOf("Narrow" to 0.5, "Normal" to 1.0, "Wide" to 1.5, "Wider" to 2.0),
            1.0,
            { it.pageMargins },
            { prefs, value -> prefs.copy(pageMargins = value) },
        )

    val pages: List<StripPage> =
        listOf(
            StripPage("Display", listOf(themeRow, fontRow, sizeRow, alignRow)),
            StripPage("Layout", listOf(lineRow, paragraphRow, lettersRow, marginsRow)),
        )

    /** The preferences after moving [row] by [delta] choices; stops at the first and last choice. */
    fun step(row: StripRow, prefs: EpubPreferences, delta: Int): EpubPreferences {
        val target = (row.indexOf(prefs) + delta).coerceIn(0, row.options.lastIndex)
        return row.options[target].apply(prefs)
    }
}
