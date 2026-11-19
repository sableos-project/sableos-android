package net.thunderbird.android.provider

import android.content.Context
import net.thunderbird.android.SableMailAppearanceReader
import net.thunderbird.android.SableMailThemeResources
import net.thunderbird.core.ui.theme.api.ThemeProvider

internal class TbThemeProvider(
    private val context: Context,
) : ThemeProvider {
    override val appThemeResourceId: Int
        get() = SableMailThemeResources.main(context)

    override val appLightThemeResourceId: Int
        get() = SableMailThemeResources.main(context, forceDark = false)

    override val appDarkThemeResourceId: Int
        get() = SableMailThemeResources.main(context, forceDark = true)

    override val dialogThemeResourceId: Int
        get() = SableMailThemeResources.dialog(context)

    override val translucentDialogThemeResourceId: Int
        get() = SableMailThemeResources.translucentDialog(context)
}
