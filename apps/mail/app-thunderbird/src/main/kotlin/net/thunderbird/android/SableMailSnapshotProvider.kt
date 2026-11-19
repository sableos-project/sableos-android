package net.thunderbird.android

import android.app.Notification
import android.app.NotificationManager
import android.content.ContentProvider
import android.content.ContentValues
import android.database.Cursor
import android.database.MatrixCursor
import android.net.Uri
import android.os.Binder
import android.os.Process

class SableMailSnapshotProvider : ContentProvider() {
    override fun onCreate(): Boolean = true

    override fun query(
        uri: Uri,
        projection: Array<out String>?,
        selection: String?,
        selectionArgs: Array<out String>?,
        sortOrder: String?,
    ): Cursor {
        val appContext = checkNotNull(context)
        enforceSableSnapshotCaller(appContext)
        val manager = appContext.getSystemService(NotificationManager::class.java)
        val count =
            runCatching {
                manager?.activeNotifications
                    .orEmpty()
                    .count { item ->
                        item.notification.category == Notification.CATEGORY_EMAIL ||
                            item.notification.category == Notification.CATEGORY_MESSAGE
                    }
            }.getOrDefault(0)

        val detail =
            when (count) {
                0 -> "no mail alerts"
                1 -> "1 mail alert"
                else -> count.toString() + " mail alerts"
            }

        return MatrixCursor(COLUMNS).apply {
            addRow(
                arrayOf<Any?>(
                    "Mail",
                    detail,
                    if (count == 0) "EMPTY" else "LIVE",
                    System.currentTimeMillis(),
                ),
            )
            setNotificationUri(appContext.contentResolver, SNAPSHOT_URI)
        }
    }

    override fun getType(uri: Uri): String =
        "vnd.android.cursor.item/vnd.org.sableos.mail.snapshot"

    override fun insert(uri: Uri, values: ContentValues?): Uri? =
        throw UnsupportedOperationException("Read-only provider")

    override fun delete(
        uri: Uri,
        selection: String?,
        selectionArgs: Array<out String>?,
    ): Int = throw UnsupportedOperationException("Read-only provider")

    override fun update(
        uri: Uri,
        values: ContentValues?,
        selection: String?,
        selectionArgs: Array<out String>?,
    ): Int = throw UnsupportedOperationException("Read-only provider")

    private fun enforceSableSnapshotCaller(context: android.content.Context) {
        val callingUid = Binder.getCallingUid()
        if (callingUid == Process.myUid()) return
        val packages =
            context.packageManager.getPackagesForUid(callingUid)
                ?.toSet()
                .orEmpty()
        val authorized =
            setOf(
                "org.sableos.launcher",
                "org.sableos.hub",
            )
        if (packages.intersect(authorized).isEmpty()) {
            throw SecurityException(
                "Sable mail snapshot is restricted to SableLauncher and SableHub.",
            )
        }
    }

    companion object {
        val SNAPSHOT_URI: Uri =
            Uri.parse("content://org.sableos.mail.snapshot/current")
        private val COLUMNS =
            arrayOf("title", "detail", "availability", "observed_at")
    }
}
