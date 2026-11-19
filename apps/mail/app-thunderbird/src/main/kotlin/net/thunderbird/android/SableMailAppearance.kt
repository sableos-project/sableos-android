package net.thunderbird.android

import android.app.Activity
import android.app.Application
import android.content.Context
import android.content.res.Configuration
import android.database.ContentObserver
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import java.lang.ref.WeakReference
import net.thunderbird.core.preference.AppTheme
import net.thunderbird.core.preference.GeneralSettingsManager
import net.thunderbird.core.preference.update

internal data class SableMailAppearance(
    val mode: String = "follow-system",
    val accent: String = "blue",
)

internal object SableMailAppearanceReader {
    private const val AUTHORITY = "org.sableos.appearance"
    private val URI = Uri.parse("content://$AUTHORITY/appearance")

    fun read(context: Context): SableMailAppearance =
        runCatching {
            context.contentResolver.query(
                URI,
                arrayOf("mode", "accent"),
                null,
                null,
                null,
            )?.use { cursor ->
                if (!cursor.moveToFirst()) {
                    return@use SableMailAppearance()
                }

                SableMailAppearance(
                    mode = cursor.getString(cursor.getColumnIndexOrThrow("mode")),
                    accent = cursor.getString(cursor.getColumnIndexOrThrow("accent")),
                )
            } ?: SableMailAppearance()
        }.getOrDefault(SableMailAppearance())

    fun uri(): Uri = URI

    fun resolveDark(
        context: Context,
        appearance: SableMailAppearance = read(context),
    ): Boolean =
        when (appearance.mode) {
            "light" -> false
            "dark" -> true
            else ->
                (
                    context.resources.configuration.uiMode and
                        Configuration.UI_MODE_NIGHT_MASK
                ) == Configuration.UI_MODE_NIGHT_YES
        }
}

internal object SableMailThemeResources {
    fun main(
        context: Context,
        forceDark: Boolean? = null,
    ): Int =
        resolveStyle(
            appearance = SableMailAppearanceReader.read(context),
            dark =
                forceDark
                    ?: SableMailAppearanceReader.resolveDark(context),
            kind = Kind.MAIN,
        )

    fun dialog(context: Context): Int =
        resolveStyle(
            appearance = SableMailAppearanceReader.read(context),
            dark = SableMailAppearanceReader.resolveDark(context),
            kind = Kind.DIALOG,
        )

    fun translucentDialog(context: Context): Int =
        resolveStyle(
            appearance = SableMailAppearanceReader.read(context),
            dark = SableMailAppearanceReader.resolveDark(context),
            kind = Kind.TRANSLUCENT,
        )

    private enum class Kind {
        MAIN,
        DIALOG,
        TRANSLUCENT,
    }

    private fun resolveStyle(
        appearance: SableMailAppearance,
        dark: Boolean,
        kind: Kind,
    ): Int =
        when (kind) {
            Kind.MAIN ->
                if (dark) {
                    when (appearance.accent) {
                        "green" -> R.style.Theme_SableMail_Dark_Green
                        "purple" -> R.style.Theme_SableMail_Dark_Purple
                        "orange" -> R.style.Theme_SableMail_Dark_Orange
                        "slate" -> R.style.Theme_SableMail_Dark_Slate
                        else -> R.style.Theme_SableMail_Dark_Blue
                    }
                } else {
                    when (appearance.accent) {
                        "green" -> R.style.Theme_SableMail_Light_Green
                        "purple" -> R.style.Theme_SableMail_Light_Purple
                        "orange" -> R.style.Theme_SableMail_Light_Orange
                        "slate" -> R.style.Theme_SableMail_Light_Slate
                        else -> R.style.Theme_SableMail_Light_Blue
                    }
                }

            Kind.DIALOG ->
                if (dark) {
                    when (appearance.accent) {
                        "green" -> R.style.Theme_SableMail_Dark_Green_Dialog
                        "purple" -> R.style.Theme_SableMail_Dark_Purple_Dialog
                        "orange" -> R.style.Theme_SableMail_Dark_Orange_Dialog
                        "slate" -> R.style.Theme_SableMail_Dark_Slate_Dialog
                        else -> R.style.Theme_SableMail_Dark_Blue_Dialog
                    }
                } else {
                    when (appearance.accent) {
                        "green" -> R.style.Theme_SableMail_Light_Green_Dialog
                        "purple" -> R.style.Theme_SableMail_Light_Purple_Dialog
                        "orange" -> R.style.Theme_SableMail_Light_Orange_Dialog
                        "slate" -> R.style.Theme_SableMail_Light_Slate_Dialog
                        else -> R.style.Theme_SableMail_Light_Blue_Dialog
                    }
                }

            Kind.TRANSLUCENT ->
                if (dark) {
                    when (appearance.accent) {
                        "green" -> R.style.Theme_SableMail_Dark_Green_Dialog_Translucent
                        "purple" -> R.style.Theme_SableMail_Dark_Purple_Dialog_Translucent
                        "orange" -> R.style.Theme_SableMail_Dark_Orange_Dialog_Translucent
                        "slate" -> R.style.Theme_SableMail_Dark_Slate_Dialog_Translucent
                        else -> R.style.Theme_SableMail_Dark_Blue_Dialog_Translucent
                    }
                } else {
                    when (appearance.accent) {
                        "green" -> R.style.Theme_SableMail_Light_Green_Dialog_Translucent
                        "purple" -> R.style.Theme_SableMail_Light_Purple_Dialog_Translucent
                        "orange" -> R.style.Theme_SableMail_Light_Orange_Dialog_Translucent
                        "slate" -> R.style.Theme_SableMail_Light_Slate_Dialog_Translucent
                        else -> R.style.Theme_SableMail_Light_Blue_Dialog_Translucent
                    }
                }
        }
}

internal object SableMailAppearanceSync {
    fun install(
        application: Application,
        generalSettingsManager: GeneralSettingsManager,
    ) {
        var current = SableMailAppearanceReader.read(application)
        var resumedActivity = WeakReference<Activity>(null)

        fun applyMode(appearance: SableMailAppearance) {
            val desired =
                when (appearance.mode) {
                    "light" -> AppTheme.LIGHT
                    "dark" -> AppTheme.DARK
                    else -> AppTheme.FOLLOW_SYSTEM
                }

            val existing =
                generalSettingsManager
                    .getConfig()
                    .display
                    .coreSettings
                    .appTheme

            if (existing != desired) {
                generalSettingsManager.update { settings ->
                    settings.copy(
                        display =
                            settings.display.copy(
                                coreSettings =
                                    settings.display.coreSettings.copy(
                                        appTheme = desired,
                                    ),
                            ),
                    )
                }
            }
        }

        applyMode(current)

        application.registerActivityLifecycleCallbacks(
            object : Application.ActivityLifecycleCallbacks {
                override fun onActivityResumed(activity: Activity) {
                    resumedActivity = WeakReference(activity)
                }

                override fun onActivityPaused(activity: Activity) = Unit
                override fun onActivityCreated(activity: Activity, state: Bundle?) = Unit
                override fun onActivityStarted(activity: Activity) = Unit
                override fun onActivityStopped(activity: Activity) = Unit
                override fun onActivitySaveInstanceState(activity: Activity, state: Bundle) = Unit

                override fun onActivityDestroyed(activity: Activity) {
                    if (resumedActivity.get() === activity) {
                        resumedActivity.clear()
                    }
                }
            },
        )

        // The appearance provider lives in Settings (Q4 framework layer). On an
        // image without it, Android throws SecurityException here; Mail keeps
        // the defaults instead of failing to start.
        runCatching {
            application.contentResolver.registerContentObserver(
                SableMailAppearanceReader.uri(),
                false,
                object : ContentObserver(Handler(Looper.getMainLooper())) {
                    override fun onChange(selfChange: Boolean) {
                        val next = SableMailAppearanceReader.read(application)
                        val modeChanged = next.mode != current.mode
                        val accentChanged = next.accent != current.accent
                        current = next

                        if (modeChanged) {
                            applyMode(next)
                        }
                        if (accentChanged) {
                            resumedActivity.get()?.recreate()
                        }
                    }
                },
            )
        }
    }
}
