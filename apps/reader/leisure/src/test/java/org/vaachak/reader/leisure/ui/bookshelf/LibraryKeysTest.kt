package org.vaachak.reader.leisure.ui.bookshelf

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

class LibraryKeysTest {
    @Test fun sortCyclesThroughEveryOrderAndWraps() {
        var order = SortOrder.PROGRESS
        val seen = mutableListOf<SortOrder>()
        repeat(4) { order = LibraryKeys.nextSort(order); seen += order }
        assertEquals(listOf(SortOrder.DATE_ADDED, SortOrder.TITLE, SortOrder.AUTHOR, SortOrder.PROGRESS), seen)
    }

    @Test fun filterCyclesAndWraps() {
        val filters = listOf("All", "Continue", "Favorites")
        assertEquals("Continue", LibraryKeys.nextFilter(filters, "All"))
        assertEquals("All", LibraryKeys.nextFilter(filters, "Favorites"))
        assertEquals("All", LibraryKeys.nextFilter(filters, "gone"))
        assertEquals("All", LibraryKeys.nextFilter(emptyList(), "All"))
    }

    @Test fun noCurrentBookInAnEmptyLibrary() {
        assertNull(LibraryKeys.currentBook(BookshelfUiState()))
    }
}
