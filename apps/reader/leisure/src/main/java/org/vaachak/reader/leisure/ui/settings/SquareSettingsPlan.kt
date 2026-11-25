package org.vaachak.reader.leisure.ui.settings

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.LibraryBooks
import androidx.compose.material.icons.filled.Audiotrack
import androidx.compose.material.icons.filled.Book
import androidx.compose.material.icons.filled.Contrast
import androidx.compose.material.icons.filled.FormatSize
import androidx.compose.material.icons.filled.Group
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.RecordVoiceOver
import androidx.compose.material.icons.filled.SwitchAccount
import androidx.compose.material.icons.outlined.Photo
import androidx.compose.ui.graphics.vector.ImageVector
import org.vaachak.reader.leisure.navigation.Screen

/** What a tile does when it is opened. */
sealed interface SquareAction {
    data class Open(val route: String) : SquareAction

    data object ToggleMultiUser : SquareAction

    data object SwitchProfile : SquareAction
}

/** One tile of the square-window Settings: a letter that opens it from anywhere on the screen. */
data class SquareTile(
    val hotkey: Char,
    val title: String,
    val subtitle: String,
    val icon: ImageVector,
    val action: SquareAction,
)

data class SquareTab(val title: String, val tiles: List<SquareTile>)

/**
 * The Settings of a square, keyboard-first window (the Q25, the Titan 2): four tabs of at most [MAX_TILES] tiles, so
 * nothing scrolls. Every setting of the scrolling list is here; only the arrangement differs.
 */
object SquareSettingsPlan {
    const val MAX_TILES = 6
    const val COLUMNS = 3

    fun tabs(multiUser: Boolean, activeProfile: String): List<SquareTab> =
        listOf(
            SquareTab(
                "App",
                buildList {
                    add(SquareTile('R', "Profiles", "Local readers", Icons.Default.Person, SquareAction.Open("reader_profiles")))
                    add(
                        SquareTile(
                            'U',
                            "Multi-user",
                            if (multiUser) "On" else "Off",
                            Icons.Default.Group,
                            SquareAction.ToggleMultiUser,
                        ),
                    )
                    if (multiUser) {
                        add(
                            SquareTile(
                                'V',
                                "Switch profile",
                                activeProfile.replaceFirstChar { it.uppercase() },
                                Icons.Default.SwitchAccount,
                                SquareAction.SwitchProfile,
                            ),
                        )
                    }
                    add(SquareTile('T', "Theme", "Follows SableOS", Icons.Default.Contrast, SquareAction.Open("settings/app_appearance")))
                    add(SquareTile('I', "About", "Version, privacy", Icons.Default.Info, SquareAction.Open(Screen.About.route)))
                },
            ),
            SquareTab(
                "Library",
                listOf(
                    SquareTile(
                        'L',
                        "Library visuals",
                        "Covers, stacks",
                        Icons.AutoMirrored.Filled.LibraryBooks,
                        SquareAction.Open(Screen.Bookshelf.route),
                    ),
                    SquareTile(
                        'B',
                        "Backup",
                        "Back up or restore",
                        Icons.AutoMirrored.Filled.LibraryBooks,
                        SquareAction.Open(Screen.Backup.route),
                    ),
                ),
            ),
            SquareTab(
                "Reading",
                listOf(
                    SquareTile('A', "Book appearance", "Fonts, margins, colour", Icons.Default.FormatSize, SquareAction.Open("settings/appearance")),
                    SquareTile('S', "Speech", "Voice, speed, timer", Icons.Default.RecordVoiceOver, SquareAction.Open("settings/tts")),
                    SquareTile('D', "Dictionaries", "Local and custom", Icons.Default.Book, SquareAction.Open("settings/dictionary")),
                ),
            ),
            SquareTab(
                "Audio & Comics",
                listOf(
                    SquareTile('P', "Audio playback", "Speed, skip lengths", Icons.Default.Audiotrack, SquareAction.Open(Screen.AudiobookDefaults.route)),
                    SquareTile('C', "Comics", "Reading mode and fit", Icons.Outlined.Photo, SquareAction.Open(Screen.ComicDefaults.route)),
                ),
            ),
        )

    /** The tile a typed letter opens, searched across every tab; null when no tile uses it. */
    fun tileForKey(tabs: List<SquareTab>, key: Char): SquareTile? =
        tabs.flatMap { it.tiles }.firstOrNull { it.hotkey.equals(key, ignoreCase = true) }

    /** The tab that holds [tile]. */
    fun tabIndexOf(tabs: List<SquareTab>, tile: SquareTile): Int = tabs.indexOfFirst { tile in it.tiles }
}
