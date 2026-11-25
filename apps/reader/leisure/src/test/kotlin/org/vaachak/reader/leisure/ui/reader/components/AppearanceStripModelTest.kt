@file:OptIn(org.readium.r2.shared.ExperimentalReadiumApi::class)

package org.vaachak.reader.leisure.ui.reader.components

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import org.readium.r2.navigator.epub.EpubPreferences

class AppearanceStripModelTest {
    private val rows = AppearanceStripModel.pages.flatMap { it.rows }

    @Test
    fun everyChoiceReadsBackAsItself() {
        rows.forEach { row ->
            row.options.forEachIndexed { index, option ->
                assertEquals("${row.label} / ${option.label}", index, row.indexOf(option.apply(EpubPreferences())))
            }
        }
    }

    @Test
    fun noPageHasMoreRowsThanFitAboveTheKeyboardStrip() {
        AppearanceStripModel.pages.forEach { assertTrue(it.title, it.rows.size <= 4) }
    }

    @Test
    fun stepMovesOneChoiceAndStopsAtTheEnds() {
        val size = rows.first { it.label == "Size" }
        val start = EpubPreferences()
        assertEquals(2, size.indexOf(start))
        assertEquals(3, size.indexOf(AppearanceStripModel.step(size, start, 1)))
        var prefs = start
        repeat(20) { prefs = AppearanceStripModel.step(size, prefs, 1) }
        assertEquals(size.options.lastIndex, size.indexOf(prefs))
        repeat(20) { prefs = AppearanceStripModel.step(size, prefs, -1) }
        assertEquals(0, size.indexOf(prefs))
    }

    @Test
    fun aSliderValueShowsTheNearestPreset() {
        val size = rows.first { it.label == "Size" }
        assertEquals(size.options.indexOfFirst { it.label == "125%" }, size.indexOf(EpubPreferences(fontSize = 1.3)))
        val margins = rows.first { it.label == "Margins" }
        assertEquals(margins.options.indexOfFirst { it.label == "Wider" }, margins.indexOf(EpubPreferences(pageMargins = 2.9)))
    }

    @Test
    fun unsetValuesShowTheNormalChoice() {
        listOf("Line height", "Paragraph", "Letters", "Margins").forEach { name ->
            val row = rows.first { it.label == name }
            assertEquals(name, row.options.indexOfFirst { it.label == "Normal" || it.label == "Small" }, row.indexOf(EpubPreferences()))
        }
    }

    @Test
    fun lowVisionRowsAreOnTheirOwnPage() {
        val vision = AppearanceStripModel.pages.first { it.title == "Vision" }
        assertEquals(listOf("Contrast", "Bold"), vision.rows.map { it.label })
    }

    @Test
    fun anyThemeChoiceClearsAHighContrastLook() {
        val contrast = rows.first { it.label == "Contrast" }
        val theme = rows.first { it.label == "Theme" }
        val yellow = contrast.options[2].apply(EpubPreferences())
        assertEquals(2, contrast.indexOf(yellow))
        assertEquals(0, contrast.indexOf(theme.options[0].apply(yellow)))
    }
}
