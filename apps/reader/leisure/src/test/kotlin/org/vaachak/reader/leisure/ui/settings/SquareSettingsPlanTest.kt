package org.vaachak.reader.leisure.ui.settings

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

class SquareSettingsPlanTest {
    private val tabs = SquareSettingsPlan.tabs(multiUser = true, activeProfile = "anna")

    @Test
    fun noTabHoldsMoreTilesThanFitWithoutScrolling() {
        tabs.forEach { assertTrue(it.title, it.tiles.size <= SquareSettingsPlan.MAX_TILES) }
    }

    @Test
    fun everyLetterOpensExactlyOneTile() {
        val letters = tabs.flatMap { it.tiles }.map { it.hotkey.uppercaseChar() }
        assertEquals(letters.size, letters.toSet().size)
    }

    @Test
    fun aLetterFindsItsTileOnAnyTabIgnoringCase() {
        val tile = SquareSettingsPlan.tileForKey(tabs, 's')
        assertNotNull(tile)
        assertEquals("Speech", tile?.title)
        assertEquals(2, SquareSettingsPlan.tabIndexOf(tabs, tile!!))
    }

    @Test
    fun anUnusedLetterOpensNothing() {
        assertNull(SquareSettingsPlan.tileForKey(tabs, 'z'))
    }

    @Test
    fun switchProfileOnlyAppearsInMultiUserMode() {
        val single = SquareSettingsPlan.tabs(multiUser = false, activeProfile = "default")
        assertNull(SquareSettingsPlan.tileForKey(single, 'V'))
        assertEquals("Anna", SquareSettingsPlan.tileForKey(tabs, 'V')?.subtitle)
    }

    @Test
    fun everySettingOfTheScrollingListIsStillReachable() {
        val routes = tabs.flatMap { it.tiles }.map { it.action }
            .filterIsInstance<SquareAction.Open>().map { it.route }.toSet()
        listOf(
            "reader_profiles", "settings/app_appearance", "settings/about", "settings/bookshelf", "settings/backup",
            "settings/appearance", "settings/tts", "settings/dictionary", "settings/audiobooks", "settings/comics",
        ).forEach { assertTrue(it, it in routes) }
    }
}
