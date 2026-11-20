package io.github.aimindseye.tenseven.ui

import android.content.Intent
import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import io.github.aimindseye.tenseven.design.sableFocusRing
import kotlinx.coroutines.delay

private const val NOW_PLAYING_POLL_MS = 1_500L

/**
 * A slim "Now playing" strip for the Home screens: cover, title, artist and Previous, Play or Pause, Next for whatever
 * media session is active (Sable Media, FM Radio, the Reader's audiobook or any other player). It reads the same
 * session as the lock screen card, so it behaves the same for every media app. Tapping the title opens the playing app.
 */
@Suppress("FunctionNaming") // Compose UI entry points use PascalCase.
@Composable
internal fun HomeNowPlayingStrip(modifier: Modifier = Modifier) {
    val context = LocalContext.current
    var now by remember { mutableStateOf<LockMediaNow?>(null) }
    LaunchedEffect(Unit) {
        while (true) {
            now = readLockMedia(context)
            delay(NOW_PLAYING_POLL_MS)
        }
    }
    val media = now ?: return
    val controls = media.controller.transportControls
    Row(
        modifier.fillMaxWidth().border(1.dp, Bb10Text.copy(alpha = 0.25f)).padding(6.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        media.art?.let { art ->
            Image(
                bitmap = art.asImageBitmap(),
                contentDescription = "Cover",
                modifier = Modifier.size(40.dp).padding(end = 8.dp),
            )
        }
        Column(
            Modifier.weight(1f).sableFocusRing().clickable {
                context.packageManager.getLaunchIntentForPackage(media.controller.packageName)
                    ?.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                    ?.let { context.startActivity(it) }
            },
        ) {
            Text(
                media.title,
                color = Bb10Text,
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
            val sub = media.artist.ifBlank { media.album }
            if (sub.isNotBlank()) {
                Text(sub, color = Bb10Text.copy(alpha = 0.7f), fontSize = 11.sp, maxLines = 1, overflow = TextOverflow.Ellipsis)
            }
        }
        NowPlayingButton("Previous", "|<") { controls.skipToPrevious() }
        NowPlayingButton(if (media.playing) "Pause" else "Play", if (media.playing) "||" else ">") {
            if (media.playing) controls.pause() else controls.play()
        }
        NowPlayingButton("Next", ">|") { controls.skipToNext() }
    }
}

@Suppress("FunctionNaming") // Compose UI entry points use PascalCase.
@Composable
private fun NowPlayingButton(description: String, glyph: String, onClick: () -> Unit) {
    Text(
        glyph,
        Modifier
            .heightIn(min = 40.dp)
            .sableFocusRing()
            .clickable(onClick = onClick)
            .semantics { contentDescription = description }
            .padding(horizontal = 10.dp, vertical = 8.dp),
        color = Bb10Text,
        fontSize = 16.sp,
        fontWeight = FontWeight.Bold,
    )
}
