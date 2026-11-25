package org.vaachak.reader.leisure.ui.bookshelf

import org.vaachak.reader.core.domain.model.BookEntity

/** Pure helpers behind the library's letter keys, so the choices can be tested without a screen. */
internal object LibraryKeys {
    private val sortCycle = listOf(SortOrder.PROGRESS, SortOrder.DATE_ADDED, SortOrder.TITLE, SortOrder.AUTHOR)

    fun nextSort(current: SortOrder): SortOrder = sortCycle[(sortCycle.indexOf(current) + 1) % sortCycle.size]

    fun sortLabel(order: SortOrder): String = when (order) {
        SortOrder.PROGRESS -> "Progress"
        SortOrder.DATE_ADDED -> "Recently added"
        SortOrder.TITLE -> "Title"
        SortOrder.AUTHOR -> "Author"
    }

    /** The next filter after [current], wrapping to the first; an unknown current filter starts from the first. */
    fun nextFilter(filters: List<String>, current: String): String {
        if (filters.isEmpty()) return current
        return filters[(filters.indexOf(current) + 1) % filters.size]
    }

    /** The book to resume: the hero book, else the first book that is started and not finished. */
    fun currentBook(state: BookshelfUiState): BookEntity? =
        state.heroBook ?: state.groupedLibrary.values.asSequence().flatten()
            .firstOrNull { it.progress > 0.0 && it.progress < 0.99 }
}
