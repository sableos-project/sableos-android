package org.sableos.reader.audio

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

/** The audiobook the playback service has open, for the library's "Now playing" bar. */
data class NowPlaying(val bookHash: String, val title: String, val playing: Boolean)

/**
 * In-process mirror of the playback service, so the library can show what is playing without binding a media
 * controller. The service and the app share one process; the service is the only writer.
 */
object AudiobookNowPlaying {
    private val mutable = MutableStateFlow<NowPlaying?>(null)
    val state: StateFlow<NowPlaying?> = mutable.asStateFlow()

    /** Set by the service; toggles play and pause on the player. Null when no service is running. */
    @Volatile
    var toggle: (() -> Unit)? = null

    internal fun opened(bookHash: String, title: String, playing: Boolean) {
        mutable.value = NowPlaying(bookHash, title, playing)
    }

    internal fun setPlaying(playing: Boolean) {
        mutable.value = mutable.value?.copy(playing = playing)
    }

    /** Hides the bar after the service stops a finished or long-paused book; the service keeps running. */
    internal fun hidden() {
        mutable.value = null
    }

    internal fun cleared() {
        mutable.value = null
        toggle = null
    }
}
