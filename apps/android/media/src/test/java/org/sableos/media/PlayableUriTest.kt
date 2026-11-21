package org.sableos.media

import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class PlayableUriTest {
    @Test
    fun aFolderPermissionCoversTheSongsInsideIt() {
        assertTrue(PlayableUri.covers("primary:Music", "primary:Music/song.mp3"))
        assertTrue(PlayableUri.covers("primary:Music", "primary:Music/Album/song.mp3"))
        assertTrue(PlayableUri.covers("primary:Music", "primary:Music"))
    }

    @Test
    fun aFolderPermissionDoesNotCoverAFolderWithASimilarName() {
        assertFalse(PlayableUri.covers("primary:Music", "primary:Music2/song.mp3"))
        assertFalse(PlayableUri.covers("primary:Music", "primary:Podcasts/song.mp3"))
    }

    @Test
    fun theRootOfTheStorageCoversEverythingOnIt() {
        assertTrue(PlayableUri.covers("primary:", "primary:Music/song.mp3"))
    }
}
