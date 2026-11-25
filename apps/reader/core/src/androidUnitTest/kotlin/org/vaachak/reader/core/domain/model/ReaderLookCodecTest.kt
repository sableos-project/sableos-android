package org.vaachak.reader.core.domain.model

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

class ReaderLookCodecTest {
    @Test
    fun aLookSurvivesBeingSavedAndRead() {
        val look = ReaderPreferences(
            theme = "contrast_yellow",
            fontSize = 1.75,
            publisherStyles = false,
            fontFamily = "OpenDyslexic",
            lineHeight = 1.5,
            pageMargins = 2.0,
            fontWeight = 1.5,
        )
        assertEquals(look, ReaderLookCodec.decode(ReaderLookCodec.encode(look)))
    }

    @Test
    fun textThatIsNotALookReadsAsNothing() {
        assertNull(ReaderLookCodec.decode(""))
        assertNull(ReaderLookCodec.decode("fontSize=2.0"))
    }
}
