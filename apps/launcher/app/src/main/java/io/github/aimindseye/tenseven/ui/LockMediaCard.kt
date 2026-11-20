package io.github.aimindseye.tenseven.ui

import android.content.ComponentName
import android.media.MediaMetadata
import android.media.session.MediaController
import android.media.session.MediaSessionManager
import android.media.session.PlaybackState
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
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
import io.github.aimindseye.tenseven.conversations.ConversationListenerService
import io.github.aimindseye.tenseven.design.sableFocusRing
import io.github.aimindseye.tenseven.platform.LockScreenMedia
import kotlinx.coroutines.delay

private const val LOCK_MEDIA_POLL_MS = 1_000L

/** What the lock screen shows for the active media session: a title line and the controller that drives it. */
internal class LockMediaNow(
    val title: String,
    val artist: String,
    val album: String,
    val art: android.graphics.Bitmap?,
    val playing: Boolean,
    val controller: MediaController,
    val positionMs: Long = 0L,
    val durationMs: Long = 0L,
    val canSeekSteps: Boolean = false,
)

/** The position now: the session's last report plus the time since, at the playback speed, while playing. */
private fun currentPosition(state: PlaybackState?): Long {
    if (state == null) return 0L
    val moved =
        if (state.state == PlaybackState.STATE_PLAYING) {
            ((android.os.SystemClock.elapsedRealtime() - state.lastPositionUpdateTime) * state.playbackSpeed).toLong()
        } else {
            0L
        }
    return (state.position + moved).coerceAtLeast(0L)
}

private fun clock(ms: Long): String {
    val total = ms / 1_000
    val h = total / 3_600
    val m = total % 3_600 / 60
    val s = total % 60
    return if (h > 0) "%d:%02d:%02d".format(h, m, s) else "%d:%02d".format(m, s)
}

internal fun readLockMedia(context: android.content.Context): LockMediaNow? =
    runCatching {
        val manager = context.getSystemService(MediaSessionManager::class.java) ?: return null
        val listener = ComponentName(context, ConversationListenerService::class.java)
        val controllers = manager.getActiveSessions(listener)
        val active =
            controllers.firstOrNull { it.playbackState?.state == PlaybackState.STATE_PLAYING } ?: controllers.firstOrNull()
        val title = active?.metadata?.getString(MediaMetadata.METADATA_KEY_TITLE).orEmpty()
        if (active == null || title.isBlank()) {
            null
        } else {
            LockMediaNow(
                title = title,
                artist = active.metadata?.getString(MediaMetadata.METADATA_KEY_ARTIST).orEmpty(),
                album = active.metadata?.getString(MediaMetadata.METADATA_KEY_ALBUM).orEmpty(),
                art =
                    active.metadata?.getBitmap(MediaMetadata.METADATA_KEY_ALBUM_ART)
                        ?: active.metadata?.getBitmap(MediaMetadata.METADATA_KEY_ART)
                        ?: active.metadata?.getBitmap(MediaMetadata.METADATA_KEY_DISPLAY_ICON),
                playing = active.playbackState?.state == PlaybackState.STATE_PLAYING,
                controller = active,
                positionMs = currentPosition(active.playbackState),
                durationMs = active.metadata?.getLong(MediaMetadata.METADATA_KEY_DURATION) ?: 0L,
                canSeekSteps =
                    (active.playbackState?.actions ?: 0L) and
                        (PlaybackState.ACTION_REWIND or PlaybackState.ACTION_FAST_FORWARD) != 0L,
            )
        }
    }.getOrNull()

/**
 * The player on the Sable lock screen: what is playing from the active media session (Sable Media, FM
 * Radio or any other player) with Previous, Play or Pause and Next. Shown only while the system
 * "media player on the lock screen" choice is on. Needs the notification access Hub already holds.
 */
@Suppress("FunctionNaming") // Compose UI entry points use PascalCase.
@Composable
internal fun LockMediaCard(modifier: Modifier = Modifier) {
    val context = LocalContext.current
    if (!LockScreenMedia.isOn(context)) return
    var now by remember { mutableStateOf<LockMediaNow?>(null) }
    LaunchedEffect(Unit) {
        while (true) {
            now = readLockMedia(context)
            delay(LOCK_MEDIA_POLL_MS)
        }
    }
    val media = now ?: return
    Column(
        modifier
            .fillMaxWidth()
            .border(1.dp, Bb10Text.copy(alpha = 0.25f))
            .padding(10.dp),
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            media.art?.let { art ->
                androidx.compose.foundation.Image(
                    bitmap = art.asImageBitmap(),
                    contentDescription = "Album art",
                    modifier = Modifier.size(96.dp).padding(end = 10.dp),
                )
            }
            Column(Modifier.weight(1f)) {
                Text(
                    media.title,
                    color = Bb10Text,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
                if (media.artist.isNotBlank()) {
                    Text(media.artist, color = Bb10Text.copy(alpha = 0.7f), fontSize = 12.sp, maxLines = 1, overflow = TextOverflow.Ellipsis)
                }
                if (media.album.isNotBlank()) {
                    Text(media.album, color = Bb10Text.copy(alpha = 0.7f), fontSize = 12.sp, maxLines = 1, overflow = TextOverflow.Ellipsis)
                }
            }
        }
        if (media.durationMs > 0L) {
            val fraction = (media.positionMs.toFloat() / media.durationMs).coerceIn(0f, 1f)
            Box(Modifier.fillMaxWidth().padding(top = 6.dp).height(3.dp).background(Bb10Text.copy(alpha = 0.2f))) {
                Box(Modifier.fillMaxWidth(fraction).height(3.dp).background(Bb10Text))
            }
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text(clock(media.positionMs), color = Bb10Text.copy(alpha = 0.7f), fontSize = 11.sp)
                Text(clock(media.durationMs), color = Bb10Text.copy(alpha = 0.7f), fontSize = 11.sp)
            }
        }
        Row(
            Modifier.fillMaxWidth().padding(top = 4.dp),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            val controls = media.controller.transportControls
            LockMediaButton("Previous", "|<") { controls.skipToPrevious() }
            if (media.canSeekSteps) LockMediaButton("Rewind", "<<") { controls.rewind() }
            LockMediaButton(if (media.playing) "Pause" else "Play", if (media.playing) "||" else ">") {
                if (media.playing) controls.pause() else controls.play()
            }
            if (media.canSeekSteps) LockMediaButton("Fast forward", ">>") { controls.fastForward() }
            LockMediaButton("Next", ">|") { controls.skipToNext() }
        }
    }
}

@Suppress("FunctionNaming") // Compose UI entry points use PascalCase.
@Composable
private fun LockMediaButton(
    description: String,
    glyph: String,
    onClick: () -> Unit,
) {
    Text(
        glyph,
        Modifier
            .heightIn(min = 44.dp)
            .sableFocusRing()
            .clickable(onClick = onClick)
            .semantics { contentDescription = description }
            .padding(horizontal = 14.dp, vertical = 10.dp),
        color = Bb10Text,
        fontSize = 18.sp,
        fontWeight = FontWeight.Bold,
    )
}
