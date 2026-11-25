// Patch history: 0017, 0080 (see docs/PATCH_HISTORY.md)
package org.sableos.reader.input

/** One line of the dismissible key-hint legend. */
data class KeyHint(val keys: String, val label: String)

/** Builds the key-hint legend from what the surface really supports, so a hint never advertises a missing feature. */
object KeyHints {
    private const val PREVIOUS_NEXT_PAGE = "Previous / next page"
    private const val PAGE_UP_DOWN = "PageUp / PageDown"
    private const val SPACE_KEYS = "Space / Shift+Space"
    private const val ADVANCE_REVERSE = "Advance / reverse"

    fun forContext(context: KeyContext): List<KeyHint> = buildList {
        navigationHints(context)?.let { addAll(it) }
        val caps = context.capabilities
        if (context.surface != ReaderSurface.AUDIO) add(KeyHint("Activate / Menu", "Show or hide controls"))
        if (caps.bookmarks) add(KeyHint("B", "Bookmark"))
        if (caps.contents) {
            add(KeyHint("T", if (context.surface == ReaderSurface.AUDIO) "Chapters" else "Contents"))
        }
        if (caps.textSearch) add(KeyHint("F", "Find"))
        if (caps.appearance) {
            val what = if (context.surface == ReaderSurface.AUDIO) "Speed, skip and sleep timer" else "Appearance"
            add(KeyHint("A", what))
        }
        if (caps.textHighlight) {
            add(KeyHint("L / Shift+L", "Highlights / bookmarks"))
            add(KeyHint("S / Shift+S", "Text bigger / smaller"))
            add(KeyHint("M", "Switch theme"))
            add(KeyHint("H", "Highlight the selection or the first paragraph"))
            add(KeyHint("K", "Key list"))
        }
        if (caps.dictionaryLookup) add(KeyHint("D", "Look up the selected word"))
        if (caps.textSearch && context.surface != ReaderSurface.AUDIO) add(KeyHint("N / Shift+N", "Next / previous find result"))
        if (caps.textToSpeech) {
            add(KeyHint("V", "Read aloud"))
            add(KeyHint(", / .", "Previous / next sentence while reading aloud"))
            add(KeyHint("- / +", "Slower / faster while reading aloud"))
        }
        add(KeyHint("Esc / Back", "Close, then leave"))
    }

    private fun navigationHints(context: KeyContext): List<KeyHint>? = when (context.surface) {
        ReaderSurface.EPUB_PAGED, ReaderSurface.PDF_PAGED, ReaderSurface.COMIC_PAGED -> listOf(
            KeyHint("Left / Right", PREVIOUS_NEXT_PAGE),
            KeyHint(PAGE_UP_DOWN, PREVIOUS_NEXT_PAGE),
            KeyHint(SPACE_KEYS, ADVANCE_REVERSE),
        )
        ReaderSurface.EPUB_SCROLL, ReaderSurface.COMIC_CONTINUOUS -> listOf(
            KeyHint("Up / Down", "Scroll"),
            KeyHint(PAGE_UP_DOWN, "Scroll a page"),
            KeyHint(SPACE_KEYS, ADVANCE_REVERSE),
        )
        ReaderSurface.COMIC_VERTICAL_PAGER -> listOf(
            KeyHint("Up / Down", PREVIOUS_NEXT_PAGE),
            KeyHint(SPACE_KEYS, ADVANCE_REVERSE),
        )
        ReaderSurface.AUDIO -> listOf(
            KeyHint("Space", "Play / pause"),
            KeyHint("Left / Right", "Seek"),
            KeyHint("P / N or $PAGE_UP_DOWN", "Previous / next chapter"),
        )
    }
}
