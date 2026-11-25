package org.vaachak.reader.leisure.ui.highlights

import org.vaachak.reader.core.domain.model.HighlightEntity

/** Writes the highlights as a Markdown file: one heading per book, one quote per highlight with its tag. */
object HighlightsExport {
    fun toMarkdown(grouped: Map<String, List<HighlightEntity>>): String = buildString {
        append("# Highlights\n")
        grouped.forEach { (book, highlights) ->
            append("\n## ").append(book).append("\n")
            highlights.forEach { highlight ->
                append("\n")
                highlight.text.trim().lines().forEach { append("> ").append(it).append("\n") }
                highlight.tag?.takeIf { it.isNotBlank() }?.let { append("\nTag: ").append(it).append("\n") }
            }
        }
    }

    const val FILE_NAME = "sable-reader-highlights.md"
}
