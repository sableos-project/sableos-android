package net.thunderbird.android.provider

import app.k9mail.core.android.common.provider.NotificationIconResourceProvider
import net.thunderbird.android.R

class TbAppIconNotificationProvider : NotificationIconResourceProvider {
    override val pushNotificationIcon: Int
        get() = R.drawable.ic_sable_mail_notification
}
