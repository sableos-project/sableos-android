package org.vaachak.reader.leisure.ui.highlights

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import org.vaachak.reader.core.domain.model.HighlightEntity

class HighlightsExportTest {
    private fun highlight(text: String, tag: String?) =
        HighlightEntity(id = "1", bookHashId = "b", profileId = "p", locatorJson = "{}", text = text, color = 0, tag = tag)

    @Test
    fun eachBookGetsAHeadingAndEachHighlightAQuoteWithItsTag() {
        val markdown = HighlightsExport.toMarkdown(
            mapOf("Dune" to listOf(highlight("Fear is the mind-killer.", "Quotes"))),
        )
        assertEquals("# Highlights\n\n## Dune\n\n> Fear is the mind-killer.\n\nTag: Quotes\n", markdown)
    }

    @Test
    fun multiLineTextStaysInsideTheQuoteAndNoTagLineIsWrittenWithoutATag() {
        val markdown = HighlightsExport.toMarkdown(mapOf("Book" to listOf(highlight("one\ntwo", null))))
        assertTrue("> one\n> two\n" in markdown)
        assertTrue("Tag:" !in markdown)
    }
}
