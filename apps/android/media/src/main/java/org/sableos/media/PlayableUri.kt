package org.sableos.media

import android.content.ContentUris
import android.content.Context
import android.net.Uri
import android.provider.DocumentsContract
import android.provider.MediaStore

/**
 * A song added through the file picker is read through a permission Android saved for its address. Android can lose that
 * permission (a folder whose permission was never kept, a new build), and the song then cannot be opened. [resolve] gives
 * such a song the address the phone's own media library uses for the same file, which the audio permission always allows.
 * A song whose permission is still there, and anything that is not a file picker address, is left as it is.
 */
internal class PlayableUri(
    private val context: Context,
) {
    private val saved: List<Uri> by lazy {
        runCatching {
            context.contentResolver.persistedUriPermissions
                .filter { it.isReadPermission }
                .map { it.uri }
        }.getOrDefault(emptyList())
    }

    fun resolve(uri: Uri): Uri {
        if (uri.authority != EXTERNAL_STORAGE) return uri
        val documentId = runCatching { DocumentsContract.getDocumentId(uri) }.getOrNull() ?: return uri
        if (hasAccess(uri, documentId)) return uri
        return mediaStoreUri(documentId) ?: uri
    }

    private fun hasAccess(
        uri: Uri,
        documentId: String,
    ): Boolean =
        saved.any { grant ->
            grant == uri ||
                (
                    grant.authority == uri.authority &&
                        DocumentsContract.isTreeUri(grant) &&
                        covers(DocumentsContract.getTreeDocumentId(grant), documentId)
                )
        }

    private fun mediaStoreUri(documentId: String): Uri? {
        // Only the phone's own storage is looked up; another volume has its own name in the media library.
        if (documentId.substringBefore(':') != PRIMARY_VOLUME) return null
        val path = documentId.substringAfter(':')
        val slash = path.lastIndexOf('/')
        val folder = if (slash >= 0) path.substring(0, slash + 1) else ""
        val name = path.substring(slash + 1)
        if (name.isEmpty()) return null
        return runCatching {
            context.contentResolver
                .query(
                    MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
                    arrayOf(MediaStore.Audio.Media._ID),
                    "${MediaStore.Audio.Media.RELATIVE_PATH} = ? AND ${MediaStore.Audio.Media.DISPLAY_NAME} = ?",
                    arrayOf(folder, name),
                    null,
                )?.use { cursor ->
                    if (cursor.moveToFirst()) {
                        ContentUris.withAppendedId(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, cursor.getLong(0))
                    } else {
                        null
                    }
                }
        }.getOrNull()
    }

    internal companion object {
        const val EXTERNAL_STORAGE = "com.android.externalstorage.documents"
        const val PRIMARY_VOLUME = "primary"

        /** Whether a saved folder permission (by its folder id) covers the document with this id. */
        fun covers(
            treeDocumentId: String,
            documentId: String,
        ): Boolean {
            val prefix = if (treeDocumentId.endsWith(":") || treeDocumentId.endsWith("/")) treeDocumentId else "$treeDocumentId/"
            return documentId == treeDocumentId || documentId.startsWith(prefix)
        }
    }
}
