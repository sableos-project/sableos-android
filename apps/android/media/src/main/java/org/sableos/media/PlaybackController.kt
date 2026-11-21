// Patch history: 0017, 0085, 0206 (see docs/PATCH_HISTORY.md)
package org.sableos.media

import android.content.ComponentName
import android.content.Context
import android.os.Handler
import android.os.Looper
import androidx.media3.common.C
import androidx.media3.common.MediaItem
import androidx.media3.common.MediaMetadata
import androidx.media3.common.PlaybackException
import androidx.media3.common.Player
import androidx.media3.session.MediaController
import androidx.media3.session.SessionToken
import org.sableos.media.MediaText.t

internal data class QueueItemUiState(
    val index: Int,
    val title: String,
    val source: String,
    /** The item's address (its media id), used to find its picture. */
    val uri: String = "",
    val album: String = "",
)

internal enum class PlaybackSkip {
    Previous,
    Next,
}

internal data class PlaybackUiState(
    val connected: Boolean = false,
    val title: String = t("nothing_queued"),
    val source: String = t("local_music_or_radio"),
    val isPlaying: Boolean = false,
    val positionMs: Long = 0L,
    val durationMs: Long = 0L,
    val hasPrevious: Boolean = false,
    val hasNext: Boolean = false,
    val shuffle: Boolean = false,
    val currentIndex: Int = -1,
    val queue: List<QueueItemUiState> = emptyList(),
    /** The album of the current track, empty when the file has none or it is radio. */
    val album: String = "",
    /** The current song could not be opened, for example because Android no longer lets Sable Media read it. */
    val failed: Boolean = false,
    /** Stop was pressed: a song is queued but nothing is playing or paused. */
    val stopped: Boolean = false,
)

internal class PlaybackController(
    context: Context,
    private val onStateChanged: (PlaybackUiState) -> Unit,
) {
    private val handler = Handler(Looper.getMainLooper())
    private var controller: MediaController? = null
    private var cachedQueue: List<QueueItemUiState> = emptyList()
    private var closed = false
    private var progressUpdatesEnabled = false
    private var shuffled = false
    private var originalOrder: List<String> = emptyList()
    private var failed = false

    private val listener =
        object : Player.Listener {
            override fun onPlayerError(error: PlaybackException) {
                failed = true
            }

            override fun onMediaItemTransition(
                mediaItem: MediaItem?,
                reason: Int,
            ) {
                failed = false
            }

            override fun onEvents(
                player: Player,
                events: Player.Events,
            ) {
                publishState(
                    refreshQueue =
                        events.contains(Player.EVENT_TIMELINE_CHANGED) ||
                            cachedQueue.size != player.mediaItemCount,
                )
            }
        }

    private val ticker = Runnable { tick() }

    init {
        val token =
            SessionToken(
                context,
                ComponentName(
                    context,
                    PlaybackService::class.java,
                ),
            )

        val future =
            MediaController
                .Builder(
                    context,
                    token,
                ).buildAsync()

        future.addListener(
            {
                runCatching { future.get() }
                    .onSuccess(::attachController)
            },
            { command -> command.run() },
        )
    }

    fun setPlaying(playing: Boolean) {
        if (playing) {
            controller?.play()
        } else {
            controller?.pause()
        }
    }

    fun stop() {
        controller?.stop()
    }

    /**
     * Shuffle reorders the songs still to come, so the Up next list shows the new order and Next follows it. Turning it
     * off puts the queue back in the order it had.
     */
    fun toggleShuffle() {
        val player = controller ?: return
        val count = player.mediaItemCount
        if (shuffled) {
            val order = originalOrder
            order.forEachIndexed { target, id ->
                val at = (0 until count).firstOrNull { player.getMediaItemAt(it).mediaId == id }
                if (at != null && at != target && target < count) {
                    player.moveMediaItem(at, target)
                }
            }
            shuffled = false
            originalOrder = emptyList()
        } else if (count > 1) {
            originalOrder = List(count) { player.getMediaItemAt(it).mediaId }
            val first = player.currentMediaItemIndex.coerceAtLeast(0) + 1
            for (slot in first until count - 1) {
                val pick = (slot until count).random()
                if (pick != slot) {
                    player.moveMediaItem(pick, slot)
                }
            }
            shuffled = true
        }
        publishState(refreshQueue = true)
    }

    private fun queueIds(player: Player): List<String> = List(player.mediaItemCount) { player.getMediaItemAt(it).mediaId }

    fun seekTo(positionMs: Long) {
        controller?.seekTo(positionMs.coerceAtLeast(0L))
    }

    fun skip(direction: PlaybackSkip) {
        when (direction) {
            PlaybackSkip.Previous -> controller?.seekToPreviousMediaItem()
            PlaybackSkip.Next -> controller?.seekToNextMediaItem()
        }
    }

    fun playQueueIndex(index: Int) {
        val player = controller ?: return
        if (index !in 0 until player.mediaItemCount) {
            return
        }

        player.seekToDefaultPosition(index)
        player.play()
    }

    fun addToQueue(
        uri: String,
        title: String,
        source: String,
    ): Boolean {
        val player = controller ?: return false
        val item =
            MediaItem
                .Builder()
                .setUri(uri)
                .setMediaId(uri)
                .setMediaMetadata(
                    MediaMetadata
                        .Builder()
                        .setTitle(title)
                        .setArtist(source)
                        .build(),
                ).build()

        player.addMediaItem(item)
        publishState(refreshQueue = true)
        return true
    }

    fun moveQueueItem(
        fromIndex: Int,
        toIndex: Int,
    ) {
        val player = controller ?: return
        val validMove =
            fromIndex in 0 until player.mediaItemCount &&
                toIndex in 0 until player.mediaItemCount &&
                fromIndex != toIndex

        if (!validMove) {
            return
        }

        player.moveMediaItem(fromIndex, toIndex)
    }

    fun removeQueueItem(index: Int) {
        val player = controller ?: return
        if (index !in 0 until player.mediaItemCount) {
            return
        }

        player.removeMediaItem(index)
    }

    fun setProgressUpdatesEnabled(enabled: Boolean) {
        if (closed || progressUpdatesEnabled == enabled) {
            return
        }

        progressUpdatesEnabled = enabled
        handler.removeCallbacks(ticker)
        if (enabled) {
            publishState(refreshQueue = false)
            handler.postDelayed(ticker, TICK_INTERVAL_MS)
        }
    }

    private fun tick() {
        if (closed || !progressUpdatesEnabled) {
            return
        }

        publishState(refreshQueue = false)
        handler.postDelayed(ticker, TICK_INTERVAL_MS)
    }

    fun close() {
        closed = true
        handler.removeCallbacksAndMessages(null)
        controller?.removeListener(listener)
        controller?.release()
        controller = null
    }

    private fun attachController(resolved: MediaController) {
        handler.post {
            if (closed) {
                resolved.release()
                return@post
            }

            controller = resolved
            resolved.addListener(listener)
            publishState(refreshQueue = true)
            if (progressUpdatesEnabled) {
                handler.postDelayed(ticker, TICK_INTERVAL_MS)
            }
        }
    }

    private fun publishState(refreshQueue: Boolean) {
        val player = controller
        if (player == null) {
            cachedQueue = emptyList()
            onStateChanged(PlaybackUiState())
            return
        }

        if (shuffled && queueIds(player).toSet() != originalOrder.toSet()) {
            shuffled = false
            originalOrder = emptyList()
        }

        if (refreshQueue) {
            cachedQueue = buildQueue(player)
        }

        onStateChanged(buildPlaybackState(player))
    }

    private fun buildQueue(player: Player): List<QueueItemUiState> =
        List(player.mediaItemCount) { index ->
            val mediaItem = player.getMediaItemAt(index)
            val metadata = mediaItem.mediaMetadata
            QueueItemUiState(
                index = index,
                uri = mediaItem.mediaId,
                title =
                    metadata.title
                        ?.toString()
                        ?.takeIf(String::isNotBlank)
                        ?: t("untitled_audio"),
                source =
                    metadata.artist
                        ?.toString()
                        ?.takeIf(String::isNotBlank)
                        ?: t("local_audio"),
                album = metadata.albumTitle?.toString().orEmpty(),
            )
        }

    private fun buildPlaybackState(player: Player): PlaybackUiState {
        val metadata = player.mediaMetadata
        return PlaybackUiState(
            connected = true,
            title =
                metadata.title
                    ?.toString()
                    ?.takeIf(String::isNotBlank)
                    ?: t("nothing_queued"),
            source =
                metadata.artist
                    ?.toString()
                    ?.takeIf(String::isNotBlank)
                    ?: t("local_music_or_radio"),
            isPlaying = player.isPlaying,
            positionMs = player.currentPosition.coerceAtLeast(0L),
            durationMs = resolvedDuration(player),
            hasPrevious = player.hasPreviousMediaItem(),
            hasNext = player.hasNextMediaItem(),
            shuffle = shuffled,
            currentIndex =
                player.currentMediaItemIndex
                    .takeIf { it != C.INDEX_UNSET }
                    ?: C.INDEX_UNSET,
            queue = cachedQueue,
            album = metadata.albumTitle?.toString().orEmpty(),
            failed = failed && player.playerError != null,
            stopped =
                player.playbackState == Player.STATE_IDLE &&
                    player.mediaItemCount > 0 &&
                    player.playerError == null,
        )
    }

    private fun resolvedDuration(player: Player): Long =
        player.duration
            .takeIf { it != C.TIME_UNSET && it > 0L }
            ?: 0L

    private companion object {
        const val TICK_INTERVAL_MS = 500L
    }
}
