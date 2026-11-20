package org.sableos.reader.ui.comic

/**
 * What the keyboard handler can ask of the current viewer. The continuous viewer installs [scrollBy]; paged viewers
 * install them only on a page that is taller or wider than the screen (page-width fit or zoomed in).
 */
class ScrollTarget {
    var scrollBy: (suspend (Float) -> Unit)? = null

    /** Sideways pan of a zoomed paged page; null when the page does not scroll sideways. */
    var panBy: (suspend (Float) -> Unit)? = null
    var canScrollDown: (() -> Boolean)? = null
    var canScrollUp: (() -> Boolean)? = null
    var viewportHeightPx: Int = 0
    var linePx: Float = 0f

    /** Pixels for one key-press line scroll; [direction] is +1 (down) or -1 (up). */
    fun line(direction: Float): Float = direction * linePx

    /** Pixels for one page scroll, a little under the viewport so the reader keeps context. */
    fun page(direction: Float): Float = direction * viewportHeightPx * PAGE_SCROLL_FRACTION

    private companion object {
        const val PAGE_SCROLL_FRACTION = 0.9f
    }
}
