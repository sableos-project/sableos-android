package net.thunderbird.android.auth

import android.net.Uri
import net.thunderbird.android.BuildConfig
import net.thunderbird.core.common.oauth.OAuthConfiguration
import net.thunderbird.core.common.oauth.OAuthConfigurationFactory

/**
 * Sable Mail ships no inherited Thunderbird OAuth client IDs. Gmail and Microsoft sign-in use the Sable
 * owner's own registrations, given at build time as SABLE_MAIL_GOOGLE_CLIENT_ID,
 * SABLE_MAIL_MICROSOFT_CLIENT_ID and SABLE_MAIL_MICROSOFT_SIGNATURE_HASH. A provider whose value is empty,
 * and every other provider, stays on IMAP/SMTP with a password.
 */
class TbOAuthConfigurationFactory : OAuthConfigurationFactory {
    override fun createConfigurations(): Map<List<String>, OAuthConfiguration> =
        buildMap {
            if (BuildConfig.SABLE_GOOGLE_CLIENT_ID.isNotBlank()) {
                put(
                    listOf(
                        "imap.gmail.com",
                        "imap.googlemail.com",
                        "smtp.gmail.com",
                        "smtp.googlemail.com",
                    ),
                    OAuthConfiguration(
                        clientId = BuildConfig.SABLE_GOOGLE_CLIENT_ID,
                        scopes = listOf("https://mail.google.com/"),
                        authorizationEndpoint = "https://accounts.google.com/o/oauth2/v2/auth",
                        tokenEndpoint = "https://oauth2.googleapis.com/token",
                        redirectUri = "${BuildConfig.APPLICATION_ID}:/oauth2redirect",
                    ),
                )
            }
            if (BuildConfig.SABLE_MICROSOFT_CLIENT_ID.isNotBlank() &&
                BuildConfig.SABLE_MICROSOFT_SIGNATURE_HASH.isNotBlank()
            ) {
                put(
                    listOf(
                        "outlook.office365.com",
                        "smtp.office365.com",
                        "smtp-mail.outlook.com",
                    ),
                    OAuthConfiguration(
                        clientId = BuildConfig.SABLE_MICROSOFT_CLIENT_ID,
                        scopes =
                            listOf(
                                "profile",
                                "openid",
                                "email",
                                "https://outlook.office.com/IMAP.AccessAsUser.All",
                                "https://outlook.office.com/SMTP.Send",
                                "offline_access",
                            ),
                        authorizationEndpoint = "https://login.microsoftonline.com/common/oauth2/v2.0/authorize",
                        tokenEndpoint = "https://login.microsoftonline.com/common/oauth2/v2.0/token",
                        redirectUri =
                            "msauth://${BuildConfig.APPLICATION_ID}/" +
                                Uri.encode(BuildConfig.SABLE_MICROSOFT_SIGNATURE_HASH),
                    ),
                )
            }
        }
}
