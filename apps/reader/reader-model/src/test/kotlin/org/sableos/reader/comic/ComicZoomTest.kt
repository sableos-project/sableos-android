package org.sableos.reader.comic

import org.junit.Assert.assertEquals
import org.junit.Test

class ComicZoomTest {
    @Test
    fun zoomingInClimbsTheLadderAndStopsAtTheTop() {
        assertEquals(150, ComicZoom.zoomIn(100))
        assertEquals(200, ComicZoom.zoomIn(150))
        assertEquals(200, ComicZoom.zoomIn(200))
    }

    @Test
    fun zoomingOutDescendsAndStopsAtTheBottom() {
        assertEquals(150, ComicZoom.zoomOut(200))
        assertEquals(100, ComicZoom.zoomOut(150))
        assertEquals(100, ComicZoom.zoomOut(100))
    }

    @Test
    fun oddValuesSnapToTheNextStep() {
        assertEquals(150, ComicZoom.zoomIn(120))
        assertEquals(100, ComicZoom.zoomOut(120))
    }
}
