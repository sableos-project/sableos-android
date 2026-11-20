package org.sableos.reader.engine.comic

import java.io.File
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Assert.fail
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TemporaryFolder

class RarComicSourceTest {
    @get:Rule val folder = TemporaryFolder()

    private fun file(name: String, bytes: ByteArray): File = folder.newFile(name).also { it.writeBytes(bytes) }

    @Test
    fun signatureIsRecognisedWhateverTheFileIsCalled() {
        val rar4 = byteArrayOf(0x52, 0x61, 0x72, 0x21, 0x1A, 0x07, 0x00, 0x01)
        val rar5 = byteArrayOf(0x52, 0x61, 0x72, 0x21, 0x1A, 0x07, 0x01, 0x00, 0x01)
        assertTrue(RarSignature.isRar(file("a.cbz", rar4)))
        assertTrue(RarSignature.isRar(file("b.cbr", rar5)))
        assertFalse(RarSignature.isRar(file("c.cbr", "PK\u0003\u0004zip".toByteArray())))
        assertFalse(RarSignature.isRar(file("d.cbr", ByteArray(0))))
        assertFalse(RarSignature.isRar(File(folder.root, "missing.cbr")))
    }

    @Test
    fun aMissingFileFailsAsNotFound() {
        try {
            RarComicSource.open(File(folder.root, "missing.cbr"))
            fail("expected a failure")
        } catch (e: ComicOpenException) {
            assertEquals(ComicOpenFailure.NOT_FOUND, e.failure)
        }
    }

    @Test
    fun aFileThatOnlyLooksLikeRarFailsToOpenInsteadOfCrashing() {
        val broken = file("broken.cbr", byteArrayOf(0x52, 0x61, 0x72, 0x21, 0x1A, 0x07, 0x00) + ByteArray(40))
        try {
            RarComicSource.open(broken).close()
            fail("expected a failure")
        } catch (e: ComicOpenException) {
            assertTrue(e.failure in setOf(ComicOpenFailure.CORRUPT, ComicOpenFailure.UNREADABLE, ComicOpenFailure.NO_PAGES))
        }
    }
}
