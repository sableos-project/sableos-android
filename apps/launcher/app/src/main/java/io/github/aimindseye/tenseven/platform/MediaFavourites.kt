package io.github.aimindseye.tenseven.platform

import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager

/** Opens the Favourites list of Sable Media (shortcuts to songs, FM and online stations, lists). */
internal object MediaFavourites {
    private const val PACKAGE = "org.sableos.media"
    private const val ACTION = "org.sableos.media.action.FAVOURITES"

    private fun intent(): Intent = Intent(ACTION).setPackage(PACKAGE).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)

    /** Sable Media is installed (the SableOS build ships it). */
    fun available(context: Context): Boolean =
        runCatching {
            context.packageManager.resolveActivity(intent(), PackageManager.MATCH_DEFAULT_ONLY) != null
        }.getOrDefault(false)

    fun open(context: Context) {
        runCatching { context.startActivity(intent()) }
    }
}
