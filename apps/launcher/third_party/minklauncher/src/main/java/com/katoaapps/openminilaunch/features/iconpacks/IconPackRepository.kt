package com.katoaapps.openminilaunch.features.iconpacks

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.graphics.drawable.Drawable
import android.os.UserManager
import androidx.core.content.ContextCompat
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

internal class IconPackRepository private constructor(context: Context) {
    private val appContext = context.applicationContext
    private val packageManager = appContext.packageManager
    private val userManager = appContext.getSystemService(UserManager::class.java)
    private val discovery = IconPackDiscovery(packageManager)
    private val definitionLoader = IconPackDefinitionLoader(packageManager)
    private val drawableLoader = IconPackDrawableLoader(appContext)
    private val revisionState = MutableStateFlow(0L)
    val revision: StateFlow<Long> = revisionState.asStateFlow()

    private val cacheLock = Any()
    private val definitions = mutableMapOf<String, IconPackDefinition>()

    private val packageReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) = invalidate()
    }

    init {
        val filter = IntentFilter().apply {
            addAction(Intent.ACTION_PACKAGE_ADDED)
            addAction(Intent.ACTION_PACKAGE_CHANGED)
            addAction(Intent.ACTION_PACKAGE_REMOVED)
            addDataScheme("package")
        }
        ContextCompat.registerReceiver(
            appContext,
            packageReceiver,
            filter,
            ContextCompat.RECEIVER_EXPORTED,
        )
    }

    fun iconForPackage(packageName: String, iconPackPackage: String): Drawable? {
        val specification = definition(iconPackPackage)?.iconForPackage(packageName) ?: return null
        return drawableLoader.load(iconPackPackage, specification)
    }

    fun invalidate() {
        synchronized(cacheLock) { definitions.clear() }
        revisionState.update { it + 1 }
    }

    private fun definition(packageName: String): IconPackDefinition? {
        synchronized(cacheLock) { definitions[packageName]?.let { return it } }
        val parsed = definitionLoader.load(packageName) ?: return null
        synchronized(cacheLock) { definitions[packageName] = parsed }
        return parsed
    }

    private fun previewIcons(packageName: String): List<Drawable> {
        val candidates = definition(packageName)?.iconsByComponent?.values.orEmpty().distinct()
        return candidates.asSequence()
            .mapNotNull { drawableLoader.load(packageName, it) }
            .take(PREVIEW_ICON_COUNT)
            .toList()
    }

    companion object {
        private const val PREVIEW_ICON_COUNT = 4

        @Volatile private var instance: IconPackRepository? = null

        fun get(context: Context): IconPackRepository = instance ?: synchronized(this) {
            instance ?: IconPackRepository(context.applicationContext).also { instance = it }
        }
    }
}
