package org.sableos.reader.comic

/** Zoom steps for a comic page, in percent of the page-width fit. Pure so the ladder is JVM-tested. */
object ComicZoom {
    const val MIN: Int = 100
    val steps: List<Int> = listOf(MIN, 150, 200)
    val MAX: Int = steps.last()

    fun zoomIn(current: Int): Int = steps.firstOrNull { it > current } ?: MAX

    fun zoomOut(current: Int): Int = steps.lastOrNull { it < current } ?: MIN
}
