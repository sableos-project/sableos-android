package app.k9mail.feature.account.setup.ui.autodiscovery

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import app.k9mail.feature.account.setup.R

private data class SableProvider(val labelRes: Int, val domain: String)

private val SableProviders =
    listOf(
        SableProvider(R.string.sable_setup_provider_gmail, "gmail.com"),
        SableProvider(R.string.sable_setup_provider_outlook, "outlook.com"),
        SableProvider(R.string.sable_setup_provider_icloud, "icloud.com"),
        SableProvider(R.string.sable_setup_provider_yahoo, "yahoo.com"),
        SableProvider(R.string.sable_setup_provider_zoho, "zoho.com"),
    )

private val SableSteps =
    listOf(
        R.string.sable_setup_step_1,
        R.string.sable_setup_step_2,
        R.string.sable_setup_step_3,
        R.string.sable_setup_step_4,
        R.string.sable_setup_step_5,
        R.string.sable_setup_step_6,
    )

/**
 * The Email Setup board's provider chips: a tap puts "@domain" in the address field when it is still empty
 * (or swaps the domain of what was typed), so the lookup can start from the provider.
 */
@OptIn(ExperimentalLayoutApi::class)
@Composable
internal fun SableProviderChips(
    emailAddress: String,
    onEmailAddressChange: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    FlowRow(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        SableProviders.forEach { provider ->
            Surface(
                shape = MaterialTheme.shapes.extraSmall,
                color = MaterialTheme.colorScheme.surface,
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
                modifier =
                    Modifier.clickable {
                        val local = emailAddress.substringBefore('@')
                        onEmailAddressChange("$local@${provider.domain}")
                    },
            ) {
                Text(
                    text = stringResource(provider.labelRes),
                    fontSize = 13.sp,
                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
                )
            }
        }
    }
}

/** The board's "How the servers are found" list, numbered 1 to 6, and its footer note. */
@Composable
internal fun SableSetupSteps(modifier: Modifier = Modifier) {
    Column(modifier.fillMaxWidth()) {
        Text(
            text = stringResource(R.string.sable_setup_steps_title).uppercase(),
            fontSize = 12.sp,
            letterSpacing = 1.4.sp,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
        Spacer(Modifier.height(6.dp))
        SableSteps.forEachIndexed { index, textRes ->
            Row(
                modifier = Modifier.fillMaxWidth().padding(vertical = 6.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalAlignment = Alignment.Top,
            ) {
                Surface(shape = CircleShape, color = MaterialTheme.colorScheme.primary, modifier = Modifier.size(24.dp)) {
                    Text(
                        text = (index + 1).toString(),
                        fontSize = 13.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.onPrimary,
                        modifier = Modifier.padding(top = 3.dp),
                        textAlign = androidx.compose.ui.text.style.TextAlign.Center,
                    )
                }
                Text(text = stringResource(textRes), fontSize = 14.sp, lineHeight = 20.sp)
            }
        }
        Spacer(Modifier.height(8.dp))
        Text(
            text = stringResource(R.string.sable_setup_footer),
            fontSize = 12.sp,
            lineHeight = 17.sp,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
    }
}
