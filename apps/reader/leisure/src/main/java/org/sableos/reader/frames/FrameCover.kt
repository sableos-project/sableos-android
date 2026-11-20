package org.sableos.reader.frames

import android.content.Context
import android.net.Uri
import android.os.Bundle
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.platform.LocalContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

/**
 * Tells the Sable launcher what this app's Active Frame should say (the SableOS cover API,
 * `content://org.sableos.launcher.covers`). Only a title and one line of text leave the app, and the launcher keeps
 * them in memory, hides them on the lock screen and lets them lapse after a day. Without the Sable launcher the call
 * finds no provider and does nothing.
 */
object FrameCover {
    private val authority: Uri = Uri.parse("content://org.sableos.launcher.covers")

    /** Blocking; call off the main thread. */
    fun set(context: Context, title: String, detail: String) {
        val extras = Bundle().apply {
            putString("title", title)
            putString("detail", detail)
        }
        runCatching { context.contentResolver.call(authority, "setCover", null, extras) }
    }

    fun clear(context: Context) {
        runCatching { context.contentResolver.call(authority, "clearCover", null, null) }
    }
}

/** Keeps the Active Frame cover in step with what the screen shows; [detail] changes re-send it. */
@Composable
fun ReportFrameCover(title: String, detail: String) {
    val context = LocalContext.current.applicationContext
    LaunchedEffect(title, detail) {
        if (title.isNotBlank() && detail.isNotBlank()) {
            withContext(Dispatchers.IO) { FrameCover.set(context, title, detail) }
        }
    }
}
