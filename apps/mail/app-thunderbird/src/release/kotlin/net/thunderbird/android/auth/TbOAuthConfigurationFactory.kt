package net.thunderbird.android.auth

import net.thunderbird.core.common.oauth.OAuthConfiguration
import net.thunderbird.core.common.oauth.OAuthConfigurationFactory

/**
 * Sable Mail deliberately ships without inherited Thunderbird OAuth client IDs.
 *
 * Generic IMAP/SMTP remains available. Gmail/Microsoft OAuth must stay hidden
 * until Sable-owned provider registrations and redirect URIs are qualified.
 */
class TbOAuthConfigurationFactory : OAuthConfigurationFactory {
    override fun createConfigurations(): Map<List<String>, OAuthConfiguration> =
        emptyMap()
}
