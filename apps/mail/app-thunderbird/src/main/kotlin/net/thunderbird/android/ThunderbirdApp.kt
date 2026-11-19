package net.thunderbird.android

import app.k9mail.feature.telemetry.api.TelemetryManager
import net.thunderbird.app.common.BaseApplication
import net.thunderbird.core.preference.GeneralSettingsManager
import org.koin.android.ext.android.inject
import org.koin.core.module.Module

class ThunderbirdApp : BaseApplication() {
    private val telemetryManager: TelemetryManager by inject()
    private val generalSettingsManager: GeneralSettingsManager by inject()

    override fun provideAppModule(): Module = appModule

    override fun onCreate() {
        super.onCreate()

        SableMailAppearanceSync.install(
            application = this,
            generalSettingsManager = generalSettingsManager,
        )
        initializeTelemetry()
    }

    private fun initializeTelemetry() {
        telemetryManager.init(
            uploadEnabled = false,
            releaseChannel = BuildConfig.GLEAN_RELEASE_CHANNEL,
            versionCode = BuildConfig.VERSION_CODE,
            versionName = BuildConfig.VERSION_NAME,
        )
    }
}
