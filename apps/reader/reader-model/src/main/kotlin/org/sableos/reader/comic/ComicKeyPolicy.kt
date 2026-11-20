package org.sableos.reader.comic

import org.sableos.reader.capability.ReaderCapabilities
import org.sableos.reader.input.KeyContext
import org.sableos.reader.input.KeyInput
import org.sableos.reader.input.KeyResolution
import org.sableos.reader.input.ReaderAction
import org.sableos.reader.input.ReaderKey
import org.sableos.reader.input.ReaderKeyMap
import org.sableos.reader.input.ReaderSurface
import org.sableos.reader.input.ReadingDirection

/** What the comic screen must do for a key press. */
enum class ComicKeyCommand {
    NEXT_PAGE,
    PREVIOUS_PAGE,
    SCROLL_LINE_DOWN,
    SCROLL_LINE_UP,
    SCROLL_PAGE_DOWN,
    SCROLL_PAGE_UP,
    TOGGLE_BOOKMARK,
    OPEN_CONTENTS,
    OPEN_APPEARANCE,
    TOGGLE_CHROME,
    ESCAPE,
    ADVANCE_IN_PAGE,
    REVERSE_IN_PAGE,
    PAN_LEFT,
    PAN_RIGHT,
    ZOOM_IN,
    ZOOM_OUT,
    ZOOM_RESET,
}

/**
 * Comic keyboard policy layered on [ReaderKeyMap]; pure, so every mode is JVM-tested. Paged modes follow the
 * reading direction (in right-to-left, Left is "next"), the vertical pager pages with Up/Down, and the continuous
 * modes scroll. Only Escape is handled while an overlay is open or a text field owns input.
 *
 * A paged page that is taller than the screen ([pageScrolls]: page-width fit or zoomed in) scrolls with Up/Down and
 * Space, which turns the page only once the end of the page is reached; when [zoomed], Left/Right pan sideways.
 * Zoom keys: Z or + zooms in, X or - zooms out, 0 resets.
 */
object ComicKeyPolicy {
    fun surfaceOf(mode: ComicReadingMode): ReaderSurface = when (mode) {
        ComicReadingMode.PAGED_LTR, ComicReadingMode.PAGED_RTL -> ReaderSurface.COMIC_PAGED
        ComicReadingMode.VERTICAL_PAGER -> ReaderSurface.COMIC_VERTICAL_PAGER
        ComicReadingMode.CONTINUOUS_VERTICAL, ComicReadingMode.WEBTOON -> ReaderSurface.COMIC_CONTINUOUS
    }

    fun decide(
        input: KeyInput,
        mode: ComicReadingMode,
        editableFocused: Boolean,
        overlayOpen: Boolean,
        pageScrolls: Boolean = false,
        zoomed: Boolean = false,
    ): ComicKeyCommand? {
        if (!editableFocused && !overlayOpen && !input.hasCommandModifier) {
            pageView(input, mode, pageScrolls, zoomed)?.let { return it }
        }
        val context = KeyContext(
            surface = surfaceOf(mode),
            capabilities = ReaderCapabilities.COMIC,
            direction = if (mode.isRightToLeft) ReadingDirection.RIGHT_TO_LEFT else ReadingDirection.LEFT_TO_RIGHT,
            editableFocused = editableFocused,
        )
        val action = (ReaderKeyMap.resolve(input, context) as? KeyResolution.Handled)?.action
        return when {
            action == null || input.key == ReaderKey.BACK -> null
            action == ReaderAction.DISMISS_OR_BACK -> ComicKeyCommand.ESCAPE
            overlayOpen -> null
            else -> command(action, mode)
        }
    }

    private fun pageView(
        input: KeyInput,
        mode: ComicReadingMode,
        pageScrolls: Boolean,
        zoomed: Boolean,
    ): ComicKeyCommand? {
        val zoomKey = if (!mode.isContinuous && !input.repeated) zoom(input) else null
        val scrolling = if (mode.isPaged && pageScrolls) scrolling(input, zoomed) else null
        return zoomKey ?: scrolling
    }

    private fun zoom(input: KeyInput): ComicKeyCommand? = if (input.key != ReaderKey.CHARACTER) {
        null
    } else {
        when (input.character?.lowercaseChar()) {
            'z', '+', '=' -> ComicKeyCommand.ZOOM_IN
            'x', '-' -> ComicKeyCommand.ZOOM_OUT
            '0' -> ComicKeyCommand.ZOOM_RESET
            else -> null
        }
    }

    private fun scrolling(input: KeyInput, zoomed: Boolean): ComicKeyCommand? = when {
        input.key == ReaderKey.DOWN -> ComicKeyCommand.SCROLL_LINE_DOWN
        input.key == ReaderKey.UP -> ComicKeyCommand.SCROLL_LINE_UP
        input.key == ReaderKey.SPACE && !input.repeated ->
            if (input.shift) ComicKeyCommand.REVERSE_IN_PAGE else ComicKeyCommand.ADVANCE_IN_PAGE
        zoomed && input.key == ReaderKey.LEFT -> ComicKeyCommand.PAN_LEFT
        zoomed && input.key == ReaderKey.RIGHT -> ComicKeyCommand.PAN_RIGHT
        else -> null
    }

    private fun command(action: ReaderAction, mode: ComicReadingMode): ComicKeyCommand? =
        movement(action, mode) ?: panel(action)

    private fun movement(action: ReaderAction, mode: ComicReadingMode): ComicKeyCommand? = when (action) {
        ReaderAction.PAGE_NEXT -> ComicKeyCommand.NEXT_PAGE
        ReaderAction.PAGE_PREVIOUS -> ComicKeyCommand.PREVIOUS_PAGE
        ReaderAction.ADVANCE -> if (mode.isContinuous) ComicKeyCommand.SCROLL_PAGE_DOWN else ComicKeyCommand.NEXT_PAGE
        ReaderAction.REVERSE -> if (mode.isContinuous) ComicKeyCommand.SCROLL_PAGE_UP else ComicKeyCommand.PREVIOUS_PAGE
        ReaderAction.SCROLL_DOWN -> ComicKeyCommand.SCROLL_LINE_DOWN
        ReaderAction.SCROLL_UP -> ComicKeyCommand.SCROLL_LINE_UP
        ReaderAction.SCROLL_PAGE_DOWN -> ComicKeyCommand.SCROLL_PAGE_DOWN
        ReaderAction.SCROLL_PAGE_UP -> ComicKeyCommand.SCROLL_PAGE_UP
        else -> null
    }

    private fun panel(action: ReaderAction): ComicKeyCommand? = when (action) {
        ReaderAction.TOGGLE_BOOKMARK -> ComicKeyCommand.TOGGLE_BOOKMARK
        ReaderAction.OPEN_CONTENTS -> ComicKeyCommand.OPEN_CONTENTS
        ReaderAction.OPEN_APPEARANCE -> ComicKeyCommand.OPEN_APPEARANCE
        ReaderAction.TOGGLE_CHROME -> ComicKeyCommand.TOGGLE_CHROME
        else -> null
    }
}
