package org.vaachak.reader.core.domain.model

/**
 * Turns a book's own reading look into a short text and back, so it can be saved under that book. One `name=value`
 * line per setting; settings that are not set are left out.
 */
object ReaderLookCodec {
    fun encode(prefs: ReaderPreferences): String = buildList {
        add("theme=${prefs.theme}")
        add("fontSize=${prefs.fontSize}")
        add("publisherStyles=${prefs.publisherStyles}")
        prefs.fontFamily?.let { add("fontFamily=$it") }
        prefs.textAlign?.let { add("textAlign=$it") }
        prefs.lineHeight?.let { add("lineHeight=$it") }
        prefs.letterSpacing?.let { add("letterSpacing=$it") }
        prefs.paragraphSpacing?.let { add("paragraphSpacing=$it") }
        prefs.pageMargins?.let { add("pageMargins=$it") }
        prefs.wordSpacing?.let { add("wordSpacing=$it") }
        prefs.paragraphIndent?.let { add("paragraphIndent=$it") }
        prefs.hyphens?.let { add("hyphens=$it") }
        prefs.ligatures?.let { add("ligatures=$it") }
        prefs.marginTop?.let { add("marginTop=$it") }
        prefs.marginBottom?.let { add("marginBottom=$it") }
        prefs.fontWeight?.let { add("fontWeight=$it") }
    }.joinToString("\n")

    /** The saved look, or null when the text is empty or not a look this app wrote. */
    fun decode(text: String): ReaderPreferences? {
        val values = text.lines().mapNotNull { line ->
            val at = line.indexOf('=')
            if (at <= 0) null else line.substring(0, at) to line.substring(at + 1)
        }.toMap()
        if (values["theme"] == null) return null
        return ReaderPreferences(
            theme = values.getValue("theme"),
            fontSize = values["fontSize"]?.toDoubleOrNull() ?: 1.0,
            publisherStyles = values["publisherStyles"]?.toBooleanStrictOrNull() ?: true,
            fontFamily = values["fontFamily"],
            textAlign = values["textAlign"],
            lineHeight = values["lineHeight"]?.toDoubleOrNull(),
            letterSpacing = values["letterSpacing"]?.toDoubleOrNull(),
            paragraphSpacing = values["paragraphSpacing"]?.toDoubleOrNull(),
            pageMargins = values["pageMargins"]?.toDoubleOrNull(),
            wordSpacing = values["wordSpacing"]?.toDoubleOrNull(),
            paragraphIndent = values["paragraphIndent"]?.toDoubleOrNull(),
            hyphens = values["hyphens"]?.toBooleanStrictOrNull(),
            ligatures = values["ligatures"]?.toBooleanStrictOrNull(),
            marginTop = values["marginTop"]?.toDoubleOrNull(),
            marginBottom = values["marginBottom"]?.toDoubleOrNull(),
            fontWeight = values["fontWeight"]?.toDoubleOrNull(),
        )
    }
}
