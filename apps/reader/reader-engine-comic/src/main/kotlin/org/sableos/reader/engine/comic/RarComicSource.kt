package org.sableos.reader.engine.comic

import com.github.junrar.Archive
import com.github.junrar.exception.RarException
import com.github.junrar.rarfile.FileHeader
import java.io.ByteArrayOutputStream
import java.io.Closeable
import java.io.File
import java.io.IOException
import java.io.OutputStream

/** True when [file] starts with a RAR signature (RAR 1.5-4.x or RAR 5), whatever the file is called. */
object RarSignature {
    private val rar4 = byteArrayOf(0x52, 0x61, 0x72, 0x21, 0x1A, 0x07, 0x00)
    private val rar5 = byteArrayOf(0x52, 0x61, 0x72, 0x21, 0x1A, 0x07, 0x01, 0x00)

    fun matches(head: ByteArray): Boolean = startsWith(head, rar4) || startsWith(head, rar5)

    fun isRar(file: File): Boolean =
        try {
            file.inputStream().use { input ->
                val head = ByteArray(rar5.size)
                val read = input.read(head)
                read > 0 && matches(head.copyOf(read))
            }
        } catch (_: IOException) {
            false
        }

    private fun startsWith(head: ByteArray, prefix: ByteArray): Boolean =
        head.size >= prefix.size && prefix.indices.all { head[it] == prefix[it] }
}

/**
 * A CBR read with junrar. Like [ZipComicSource], pages are extracted on demand into memory, never to disk, and every
 * read is capped on the bytes actually produced; structural limits fail the open and per-page limits fail only that
 * page. A page of a solid archive makes the library unpack what comes before it, which is slower than a CBZ.
 */
class RarComicSource private constructor(
    private val archive: Archive,
    private val headers: List<FileHeader>,
    override val limits: ComicLimits,
    override val catalog: ComicCatalog,
    override val info: ComicInfo?,
) : ComicSource, Closeable by archive {
    override fun readPage(index: Int): ByteArray {
        val page = catalog.pages.getOrNull(index) ?: throw ComicPageException(PageFailure.UNREADABLE, "no page $index")
        ComicCatalogBuilder.checkReadable(page, limits)
        return extract(page.handle, limits.maxPageBytes.toLong())
    }

    private fun extract(handle: String, cap: Long): ByteArray {
        val header = handle.toIntOrNull()?.let(headers::getOrNull)
            ?: throw ComicPageException(PageFailure.UNREADABLE, "entry vanished")
        if (header.isEncrypted) throw ComicPageException(PageFailure.ENCRYPTED)
        val out = CappedOutput(cap)
        try {
            synchronized(archive) { archive.extractFile(header, out) }
        } catch (e: RarException) {
            throw failure(out, e)
        } catch (e: IOException) {
            throw failure(out, e)
        }
        if (out.overflowed) throw ComicPageException(PageFailure.TOO_LARGE, "page exceeds $cap bytes")
        return out.toByteArray()
    }

    private fun failure(out: CappedOutput, e: Exception): ComicPageException =
        if (out.overflowed) {
            ComicPageException(PageFailure.TOO_LARGE, "page exceeds ${out.cap} bytes", e)
        } else {
            ComicPageException(PageFailure.UNREADABLE, e.message, e)
        }

    /** Keeps at most [cap] bytes; one more byte means the header lied about the size, so extraction is aborted. */
    private class CappedOutput(val cap: Long) : OutputStream() {
        private val bytes = ByteArrayOutputStream()
        var overflowed = false
            private set

        override fun write(b: Int) = write(byteArrayOf(b.toByte()), 0, 1)

        override fun write(b: ByteArray, off: Int, len: Int) {
            if (bytes.size() + len.toLong() > cap) {
                overflowed = true
                throw IOException("page exceeds $cap bytes")
            }
            bytes.write(b, off, len)
        }

        fun toByteArray(): ByteArray = bytes.toByteArray()
    }

    companion object {
        fun open(file: File, limits: ComicLimits = ComicLimits()): ComicSource {
            if (!file.isFile) throw ComicOpenException(ComicOpenFailure.NOT_FOUND)
            val archive = try {
                Archive(file)
            } catch (e: RarException) {
                throw ComicOpenException(ComicOpenFailure.CORRUPT, e.message, e)
            } catch (e: IOException) {
                throw ComicOpenException(ComicOpenFailure.UNREADABLE, e.message, e)
            }
            try {
                if (archive.isPasswordProtected || archive.isEncrypted) {
                    throw ComicOpenException(ComicOpenFailure.ENCRYPTED)
                }
                val headers = archive.fileHeaders.toList()
                val raw = headers.asSequence().withIndex().filter { !it.value.isDirectory }.map { (i, h) ->
                    RawEntry(h.fileName.replace('\\', '/'), h.fullUnpackSize, h.fullPackSize, i.toString())
                }
                val catalog = ComicCatalogBuilder.build(raw, limits)
                val source = RarComicSource(archive, headers, limits, catalog, null)
                val info = catalog.comicInfoHandle?.let { handle ->
                    runCatching { ComicInfoParser.parse(source.extract(handle, limits.maxComicInfoBytes.toLong()), limits) }.getOrNull()
                }
                return RarComicSource(archive, headers, limits, catalog, info)
            } catch (e: ComicOpenException) {
                archive.close()
                throw e
            } catch (e: RarException) {
                archive.close()
                throw ComicOpenException(ComicOpenFailure.CORRUPT, e.message, e)
            } catch (e: IllegalStateException) {
                archive.close()
                throw ComicOpenException(ComicOpenFailure.CORRUPT, e.message, e)
            }
        }
    }
}
