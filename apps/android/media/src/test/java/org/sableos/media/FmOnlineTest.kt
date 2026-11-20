package org.sableos.media

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test
import java.io.ByteArrayInputStream

class FmOnlineTest {
    @Test
    fun `frequency is read from the station name`() {
        assertEquals(1069, FmDirectoryParser.frequencyOf("Classic 106.9 FM"))
        assertEquals(875, FmDirectoryParser.frequencyOf("Radio 87,5"))
        assertNull(FmDirectoryParser.frequencyOf("Radio 1069"))
        assertNull(FmDirectoryParser.frequencyOf("Station 120.5"))
    }

    @Test
    fun `directory keeps the first station per frequency and only https streams`() {
        val json =
            """[{"name":"Hits 106.1","tags":"pop,hits","url_resolved":"https://a/s"},
               {"name":"Other 106.1","tags":"rock","url_resolved":"https://b/s"},
               {"name":"Old 99.3","tags":"","url_resolved":"http://c/s"},
               {"name":"No frequency","tags":"x","url_resolved":"https://d/s"}]"""
        val map = FmDirectoryParser.parse(json)
        assertEquals(setOf(1061, 993), map.keys)
        assertEquals("Hits 106.1", map.getValue(1061).name)
        assertEquals("Pop", map.getValue(1061).genre)
        assertEquals("https://a/s", map.getValue(1061).streamUrl)
        assertEquals("", map.getValue(993).streamUrl)
    }

    @Test
    fun `directory survives the cache round trip`() {
        val map = mapOf(1069 to FmDirectoryEntry(1069, "Classic 106.9", "Classic", "https://x"))
        assertEquals(map, FmDirectoryParser.decode(FmDirectoryParser.encode(map)))
    }

    @Test
    fun `stream title and artist split`() {
        assertEquals("Phil Collins - In the Air", IcyMetadata.streamTitle("StreamTitle='Phil Collins - In the Air';StreamUrl='';"))
        assertNull(IcyMetadata.streamTitle("StreamTitle='';"))
        assertEquals("Phil Collins" to "In the Air", IcyMetadata.split("Phil Collins - In the Air"))
        assertEquals("" to "Jingle", IcyMetadata.split("Jingle"))
    }

    @Test
    fun `metadata block is read after the interval`() {
        val meta = "StreamTitle='A - B';".padEnd(32, '\u0000').toByteArray()
        val bytes = ByteArray(10) + byteArrayOf(2) + meta
        assertEquals("A - B", IcyMetadata.read(ByteArrayInputStream(bytes), 10))
        assertNull(IcyMetadata.read(ByteArrayInputStream(ByteArray(10) + byteArrayOf(0)), 10))
    }
}
