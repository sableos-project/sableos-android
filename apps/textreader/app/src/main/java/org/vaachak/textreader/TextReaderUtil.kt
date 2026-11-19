package org.vaachak.textreader

import java.io.FilterInputStream
import java.io.InputStream

object TextReaderUtil {
    private const val MAX_TEXT_BYTES = 2 * 1024 * 1024L

    fun readTextFromStream(inputStream: InputStream?): String {
        if (inputStream == null) return "Error: File stream is null."

        return try {
            BoundedInputStream(inputStream, MAX_TEXT_BYTES)
                .bufferedReader()
                .use { it.readText() }
                .trim()
        } catch (e: Exception) {
            "Error reading file: ${e.message}"
        }
    }

    private class BoundedInputStream(
        input: InputStream,
        private var remaining: Long,
    ) : FilterInputStream(input) {
        override fun read(): Int {
            if (remaining <= 0) return -1
            val value = super.read()
            if (value >= 0) remaining -= 1
            return value
        }

        override fun read(
            buffer: ByteArray,
            offset: Int,
            length: Int,
        ): Int {
            if (remaining <= 0) return -1
            val allowed = minOf(length.toLong(), remaining).toInt()
            val count = super.read(buffer, offset, allowed)
            if (count > 0) remaining -= count.toLong()
            return count
        }
    }
}
