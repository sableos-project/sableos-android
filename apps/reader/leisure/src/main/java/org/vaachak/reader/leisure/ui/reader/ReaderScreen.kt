// Patch history: 0017 (see docs/PATCH_HISTORY.md)
package org.vaachak.reader.leisure.ui.reader

// --- MAESTRO FIX 1: Import semantics ---
import org.sableos.reader.ui.Bb7ConfirmDialog
import android.app.Activity
import android.view.ActionMode
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.FrameLayout
import androidx.activity.compose.BackHandler
import androidx.activity.compose.LocalActivity
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.focusable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.input.key.KeyEventType
import androidx.compose.ui.input.key.onPreviewKeyEvent
import androidx.compose.ui.input.key.type
import androidx.compose.ui.input.key.utf16CodePoint
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.SkipNext
import androidx.compose.material.icons.filled.SkipPrevious
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material.icons.filled.WarningAmber
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledIconButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.compose.ui.zIndex
import androidx.core.net.toUri
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.compose.LocalLifecycleOwner
import kotlinx.coroutines.launch
import org.readium.navigator.media.tts.android.AndroidTtsEngine.Voice
import org.readium.r2.navigator.Decoration
import org.readium.r2.navigator.DecorableNavigator
import org.readium.r2.navigator.epub.EpubNavigatorFactory
import org.readium.r2.navigator.epub.EpubNavigatorFragment
import org.readium.r2.navigator.epub.EpubPreferences
import org.readium.r2.navigator.input.InputListener
import org.readium.r2.navigator.input.TapEvent
import org.readium.r2.navigator.preferences.Theme
import org.readium.r2.shared.ExperimentalReadiumApi
import org.readium.r2.shared.publication.Locator
import org.readium.r2.shared.publication.Publication
import org.readium.r2.shared.util.AbsoluteUrl
import org.sableos.design.AppearanceMode
import org.sableos.design.rememberGlobalSableAppearance
import org.vaachak.reader.core.domain.model.HighlightEntity
import org.vaachak.reader.leisure.ui.reader.components.DictionaryBottomSheet
import org.sableos.reader.layout.rememberReaderLayoutProfile
import org.sableos.reader.input.EpubKeyCommand
import org.sableos.reader.ui.epub.EpubKeyboardEffect
import org.vaachak.reader.leisure.ui.reader.components.BookHighlightsOverlay
import org.vaachak.reader.leisure.ui.reader.components.BookSearchOverlay
import org.vaachak.reader.leisure.ui.reader.components.ReaderAppearanceStrip
import org.vaachak.reader.leisure.ui.reader.components.ReaderSettingsSheet
import org.vaachak.reader.leisure.ui.reader.components.ReaderSystemFooter
import org.vaachak.reader.leisure.ui.reader.components.ReaderTopBar
import org.vaachak.reader.leisure.ui.reader.components.TableOfContents
import org.vaachak.reader.leisure.ui.reader.components.TtsSettingsBottomSheet
import org.vaachak.reader.leisure.ui.testability.Tid
import org.vaachak.reader.leisure.ui.testability.TidScreen
import org.vaachak.reader.leisure.ui.testability.tid
import org.vaachak.reader.leisure.ui.testability.tids
import kotlin.math.roundToInt

private const val MENU_HIGHLIGHT = 102
private const val MENU_DEFINE = 103
private const val USER_HIGHLIGHTS = "user_highlights"
private const val TTS_HIGHLIGHTS = "tts_highlights"
private const val EPUB_READER_FRAGMENT = "EPUB_READER_FRAGMENT"

@OptIn(ExperimentalMaterial3Api::class, ExperimentalReadiumApi::class)
@Composable
fun ReaderScreen(
    initialUri: String?,
    initialLocatorJson: String?,
    onBack: () -> Unit,
    onTtsStatusChange: (Boolean) -> Unit,
    viewModel: ReaderViewModel = hiltViewModel()
) {
    val activity = LocalActivity.current as AppCompatActivity

    val publication by viewModel.publication.collectAsState()

    val showToc by viewModel.showToc.collectAsState()
    val showReaderSettings by viewModel.showReaderSettings.collectAsState()
    val epubPreferences by viewModel.epubPreferences.collectAsState()
    val effectiveEpubPreferences = rememberEffectiveEpubPreferences(epubPreferences)

    val showSearch by viewModel.showSearch.collectAsState()
    val showHighlights by viewModel.showHighlights.collectAsState()
    val showBookmarks by viewModel.showBookmarks.collectAsState()
    val isPageBookmarked by viewModel.isCurrentPageBookmarked.collectAsState()

    // TTS State
    val isTtsActive by viewModel.isTtsActive.collectAsState()

    var showDeleteDialogId by remember { mutableStateOf<String?>(null) }
    var showKeyList by remember { mutableStateOf(false) }
    val ttsProblem by viewModel.ttsProblem.collectAsState()

    val isBottomSheetVisible by viewModel.isBottomSheetVisible.collectAsState()
    val showTagSelector by viewModel.showTagSelector.collectAsState()
    val pageInfo by viewModel.currentPageInfo.collectAsState()
    val savedHighlights by viewModel.currentBookHighlights.collectAsState()
    val snackbarMessage by viewModel.snackbarMessage.collectAsState()

    val scope = rememberCoroutineScope()
    var currentNavigatorFragment by remember { mutableStateOf<EpubNavigatorFragment?>(null) }
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(snackbarMessage) {
        snackbarMessage?.let {
            snackbarHostState.showSnackbar(it)
            viewModel.clearSnackbar()
        }
    }

    LaunchedEffect(isTtsActive) {
        onTtsStatusChange(isTtsActive)
    }

    NavigatorStateEffects(
        navigator = currentNavigatorFragment,
        hasPublication = publication != null,
        preferences = effectiveEpubPreferences,
        savedHighlights = savedHighlights,
        viewModel = viewModel
    )
    SaveProgressOnPauseEffect(viewModel)
    HideSystemBarsEffect()

    LaunchedEffect(initialUri, initialLocatorJson) {
        if (initialUri != null) {
            viewModel.setInitialLocation(initialLocatorJson)
            viewModel.onFileSelected(initialUri.toUri())
        }
    }

    val navListener = remember {
        object : EpubNavigatorFragment.Listener {
            override fun onJumpToLocator(locator: Locator) { viewModel.updateProgress(locator) }
            override fun onExternalLinkActivated(url: AbsoluteUrl) {
                // Sable Reader is offline: links that leave the book are deliberately not opened.
            }
        }
    }

    // Compact and square-ish windows show reading chrome only on demand; larger windows keep it.
    val layoutProfile = rememberReaderLayoutProfile()
    var chromeVisible by remember(layoutProfile.transientChrome) { mutableStateOf(layoutProfile.chromeInitiallyVisible) }
    val transientChrome by rememberUpdatedState(layoutProfile.transientChrome)

    val overlayOpen = showDeleteDialogId != null || showKeyList || ttsProblem != null || showTagSelector || showReaderSettings || showToc ||
        showSearch || showHighlights || showBookmarks || isBottomSheetVisible
    EpubKeyboardEffect(
        scrollMode = epubPreferences.scroll == true,
        overlayOpen = overlayOpen,
        navigator = { currentNavigatorFragment },
        onCommand = { command ->
            when (command) {
                EpubKeyCommand.TOGGLE_BOOKMARK -> viewModel.toggleBookmarkOnCurrentPage()
                EpubKeyCommand.OPEN_CONTENTS -> viewModel.toggleToc()
                EpubKeyCommand.OPEN_FIND -> viewModel.toggleSearch()
                EpubKeyCommand.OPEN_APPEARANCE -> viewModel.toggleReaderSettings()
                EpubKeyCommand.OPEN_HIGHLIGHTS -> viewModel.toggleHighlights()
                EpubKeyCommand.OPEN_BOOKMARKS -> viewModel.toggleBookmarksList()
                EpubKeyCommand.TEXT_LARGER -> viewModel.adjustTextSize(1)
                EpubKeyCommand.TEXT_SMALLER -> viewModel.adjustTextSize(-1)
                EpubKeyCommand.CYCLE_THEME -> viewModel.cycleReaderTheme()
                EpubKeyCommand.TOGGLE_SPEECH -> viewModel.toggleTts()
                EpubKeyCommand.SHOW_KEYS -> showKeyList = true
                EpubKeyCommand.FIND_NEXT -> viewModel.stepFindResult(1)
                EpubKeyCommand.FIND_PREVIOUS -> viewModel.stepFindResult(-1)
                EpubKeyCommand.HIGHLIGHT -> scope.launch {
                    val navigator = currentNavigatorFragment
                    val target = navigator?.currentSelection()?.locator ?: navigator?.firstVisibleElementLocator()
                    if (target != null) viewModel.prepareHighlight(target) else viewModel.showMessage("Nothing to highlight here")
                }
                EpubKeyCommand.LOOKUP_WORD -> scope.launch {
                    val word = currentNavigatorFragment?.currentSelection()?.locator?.text?.highlight
                    if (word.isNullOrBlank()) viewModel.showMessage("Select a word first, then press D") else viewModel.lookupWord(word)
                }
                EpubKeyCommand.SPEECH_PREVIOUS_SENTENCE -> viewModel.skipSentence(-1)
                EpubKeyCommand.SPEECH_NEXT_SENTENCE -> viewModel.skipSentence(1)
                EpubKeyCommand.SPEECH_SLOWER -> viewModel.adjustTtsSpeed(-1)
                EpubKeyCommand.SPEECH_FASTER -> viewModel.adjustTtsSpeed(1)
                EpubKeyCommand.TOGGLE_CHROME -> chromeVisible = !chromeVisible
                EpubKeyCommand.ESCAPE -> activity.onBackPressedDispatcher.onBackPressed()
                else -> Unit
            }
        },
    )

    BackHandler {
        when {
            showDeleteDialogId != null -> showDeleteDialogId = null
            showKeyList -> showKeyList = false
            ttsProblem != null -> viewModel.dismissTtsProblem()
            showTagSelector -> viewModel.dismissTagSelector()
            showReaderSettings -> viewModel.dismissReaderSettings()
            showToc -> viewModel.toggleToc()
            showSearch -> viewModel.toggleSearch()
            showHighlights -> viewModel.toggleHighlights()
            showBookmarks -> viewModel.toggleBookmarksList()
            isBottomSheetVisible -> viewModel.dismissBottomSheet()
            transientChrome && chromeVisible -> chromeVisible = false
            else -> {
                viewModel.closeBook()
                onBack()
            }
        }
    }

    NavigatorListenersEffect(
        navigator = currentNavigatorFragment,
        savedHighlights = savedHighlights,
        onHighlightActivated = { showDeleteDialogId = it },
        onCenterTap = { if (transientChrome) chromeVisible = !chromeVisible else viewModel.toggleReaderSettings() }
    )

    val selectionCallback = rememberSelectionCallback(viewModel) { currentNavigatorFragment }

    TidScreen(Tid.Screen.reader) {
        Scaffold(
            topBar = {
                if (chromeVisible) ReaderTopBar(
                    bookTitle = publication?.metadata?.title ?: "Loading...",
                    isBookmarked = isPageBookmarked,
                    isTtsActive = isTtsActive,
                    onBack = { viewModel.closeBook(); onBack() },
                    onTocClick = { viewModel.toggleToc() },
                    onSearchClick = { viewModel.toggleSearch() },
                    onHighlightsClick = { viewModel.toggleHighlights() },
                    onBookmarksListClick = { viewModel.toggleBookmarksList() },
                    onBookmarkToggleClick = { viewModel.toggleBookmarkOnCurrentPage() },
                    onTtsClick = { viewModel.toggleTts() },
                    onSettingsClick = { viewModel.toggleReaderSettings() }
                )
            },
            bottomBar = {
                val panelOpen = showReaderSettings || showToc || showSearch || showHighlights || showBookmarks
                Column (
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.Bottom
                ){
                    if (chromeVisible && publication != null && !panelOpen) {
                        ReaderSystemFooter(chapterTitle = pageInfo)
                    }
                }
            },
            snackbarHost = {
                SnackbarHost(
                    hostState = snackbarHostState,
                    modifier = if (snackbarMessage == "Test highlight created") Modifier.tid(Tid.Reader.highlightCreatedBanner) else Modifier
                )
            }
        ) { padding ->
            Box(modifier = Modifier.padding(padding).fillMaxSize(), contentAlignment = Alignment.Center) {
                val loaded = publication
                if (loaded == null) {
                    CircularProgressIndicator(color = Color.Black)
                } else {
                    EpubNavigatorHost(
                        activity = activity,
                        publication = loaded,
                        viewModel = viewModel,
                        selectionCallback = selectionCallback,
                        navListener = navListener,
                        onFragmentCreated = { fragment ->
                            scope.launch { fragment.currentLocator.collect { locator -> viewModel.updateProgress(locator) } }
                            currentNavigatorFragment = fragment
                        }
                    )
                }
                ReaderPanels(viewModel = viewModel)

                if (showKeyList) ReaderKeyListDialog(onDismiss = { showKeyList = false })
                ttsProblem?.let { problem -> TtsProblemDialog(problem, onDismiss = { viewModel.dismissTtsProblem() }) }

                showDeleteDialogId?.let { highlightId ->
                    DeleteHighlightDialog(
                        onConfirm = { viewModel.deleteHighlight(highlightId); showDeleteDialogId = null },
                        onDismiss = { showDeleteDialogId = null }
                    )
                }

                ReaderTtsControls(viewModel = viewModel)
            }
        }
    }
}

/**
 * Binds the Readium EPUB canvas to SableOS Light/Dark so book content and chrome never disagree.
 * Sepia remains a deliberate reading-only override.
 */
@OptIn(ExperimentalReadiumApi::class)
@Composable
private fun rememberEffectiveEpubPreferences(epubPreferences: EpubPreferences): EpubPreferences {
    val sableAppearance = rememberGlobalSableAppearance()
    val systemDark = isSystemInDarkTheme()
    val sableDark =
        when (sableAppearance.mode) {
            AppearanceMode.FollowSystem -> systemDark
            AppearanceMode.Light -> false
            AppearanceMode.Dark -> true
        }
    return remember(epubPreferences, sableAppearance, systemDark) {
        when {
            // Sepia is the only normal page-colour override retained independently of the
            // operating-system Light/Dark selection.
            epubPreferences.theme == Theme.SEPIA -> epubPreferences

            // The high-contrast looks set their own page and text colours.
            epubPreferences.backgroundColor != null -> epubPreferences

            else ->
                epubPreferences.copy(
                    theme = if (sableDark) Theme.DARK else Theme.LIGHT,
                )
        }
    }
}

/** Keeps the navigator in step with the view model: TTS, decorations, preferences and navigation requests. */
@OptIn(ExperimentalReadiumApi::class)
@Composable
private fun NavigatorStateEffects(
    navigator: EpubNavigatorFragment?,
    hasPublication: Boolean,
    preferences: EpubPreferences,
    savedHighlights: List<Decoration>,
    viewModel: ReaderViewModel,
) {
    val currentNavigator by rememberUpdatedState(navigator)
    val ttsDecoration by viewModel.ttsDecoration.collectAsState()

    LaunchedEffect(navigator) {
        if (navigator != null) {
            viewModel.setVisualNavigatorProvider { currentNavigator }
        }
    }

    LaunchedEffect(navigator, hasPublication) {
        if (navigator != null && hasPublication) {
            viewModel.initTts()
        }
    }

    LaunchedEffect(ttsDecoration, navigator) {
        if (navigator?.view != null) {
            navigator.applyDecorations(ttsDecoration, TTS_HIGHLIGHTS)
        }
    }

    LaunchedEffect(Unit) { viewModel.navigationEvent.collect { link -> currentNavigator?.go(link, animated = false) } }
    LaunchedEffect(Unit) { viewModel.jumpEvent.collect { locator -> currentNavigator?.go(locator, animated = false) } }

    LaunchedEffect(preferences, navigator) {
        if (navigator?.view != null) {
            navigator.submitPreferences(preferences)
        }
    }

    LaunchedEffect(savedHighlights, navigator) {
        if (navigator?.view != null) {
            navigator.applyDecorations(savedHighlights, USER_HIGHLIGHTS)
        }
    }
}

/** Saves the reading position whenever the screen pauses and when it leaves composition. */
@Composable
private fun SaveProgressOnPauseEffect(viewModel: ReaderViewModel) {
    val lifecycleOwner = LocalLifecycleOwner.current
    DisposableEffect(lifecycleOwner) {
        val savePosition = {
            val json = viewModel.currentLocator.value?.toJSON()?.toString() ?: ""
            viewModel.onReaderPause(json)
        }
        val observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_PAUSE) savePosition()
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
            savePosition()
        }
    }
}

/** Reading is full screen: system bars are hidden while the reader is shown and restored on exit. */
@Composable
private fun HideSystemBarsEffect() {
    val view = LocalView.current
    DisposableEffect(Unit) {
        val window = (view.context as? Activity)?.window
        val insetsController = window?.let { WindowCompat.getInsetsController(it, view) }
        insetsController?.systemBarsBehavior = WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
        insetsController?.hide(WindowInsetsCompat.Type.systemBars())
        onDispose { insetsController?.show(WindowInsetsCompat.Type.systemBars()) }
    }
}

/** Attaches the highlight-tap and centre-tap listeners to the navigator fragment for as long as it is shown. */
@OptIn(ExperimentalReadiumApi::class)
@Composable
private fun NavigatorListenersEffect(
    navigator: EpubNavigatorFragment?,
    savedHighlights: List<Decoration>,
    onHighlightActivated: (String) -> Unit,
    onCenterTap: () -> Unit,
) {
    val activity = LocalActivity.current as AppCompatActivity
    val view = LocalView.current
    val scope = rememberCoroutineScope()
    val latestHighlights by rememberUpdatedState(savedHighlights)
    val latestOnHighlightActivated by rememberUpdatedState(onHighlightActivated)
    val latestOnCenterTap by rememberUpdatedState(onCenterTap)

    val decorationListener = remember {
        object : DecorableNavigator.Listener {
            override fun onDecorationActivated(event: DecorableNavigator.OnActivatedEvent): Boolean {
                if (event.group != USER_HIGHLIGHTS) return false
                latestOnHighlightActivated(event.decoration.id)
                return true
            }
        }
    }

    val inputListener = remember {
        object : InputListener {
            override fun onTap(event: TapEvent): Boolean {
                val screenWidth = view.width.toFloat()
                val x = event.point.x
                val inCenter = x > screenWidth * 0.2f && x < screenWidth * 0.8f
                if (inCenter) latestOnCenterTap()
                return inCenter
            }
        }
    }

    DisposableEffect(navigator) {
        if (navigator == null) return@DisposableEffect onDispose {}
        val fm = activity.supportFragmentManager
        val applyHighlights = { scope.launch { navigator.applyDecorations(latestHighlights, USER_HIGHLIGHTS) } }

        val lifecycleObserver = object : DefaultLifecycleObserver {
            override fun onResume(owner: LifecycleOwner) {
                if (navigator.isAdded && navigator.view != null) applyHighlights()
            }
        }

        val fmCallbacks = object : FragmentManager.FragmentLifecycleCallbacks() {
            override fun onFragmentStarted(fm: FragmentManager, f: Fragment) {
                if (f == navigator) {
                    navigator.addDecorationListener(USER_HIGHLIGHTS, decorationListener)
                    f.lifecycle.addObserver(lifecycleObserver)
                    applyHighlights()
                }
            }
        }

        if (navigator.isAdded) {
            navigator.addDecorationListener(USER_HIGHLIGHTS, decorationListener)
            navigator.addInputListener(inputListener)
            navigator.lifecycle.addObserver(lifecycleObserver)
            applyHighlights()
        }
        fm.registerFragmentLifecycleCallbacks(fmCallbacks, false)

        onDispose {
            fm.unregisterFragmentLifecycleCallbacks(fmCallbacks)
            if (navigator.isAdded) {
                try {
                    navigator.removeDecorationListener(decorationListener)
                    navigator.lifecycle.removeObserver(lifecycleObserver)
                } catch (_: Exception) {
                    // The fragment can be torn down while the screen leaves; there is nothing left to detach.
                }
            }
        }
    }
}

/** The text-selection menu: Highlight and Define act on the navigator's current selection. */
@Composable
private fun rememberSelectionCallback(
    viewModel: ReaderViewModel,
    navigator: () -> EpubNavigatorFragment?,
): ActionMode.Callback {
    val scope = rememberCoroutineScope()
    val latestNavigator by rememberUpdatedState(navigator)
    return remember {
        object : ActionMode.Callback {
            override fun onCreateActionMode(mode: ActionMode?, menu: Menu?): Boolean {
                menu?.add(Menu.NONE, MENU_HIGHLIGHT, 0, "Highlight")
                menu?.add(Menu.NONE, MENU_DEFINE, 1, "Define")
                return true
            }
            override fun onPrepareActionMode(mode: ActionMode?, menu: Menu?) = false
            override fun onActionItemClicked(mode: ActionMode?, item: MenuItem?): Boolean {
                scope.launch {
                    val selection = latestNavigator()?.currentSelection()
                    val locator = selection?.locator ?: return@launch
                    val text = locator.text.highlight ?: ""
                    when (item?.itemId) {
                        MENU_HIGHLIGHT -> viewModel.prepareHighlight(locator)
                        MENU_DEFINE -> viewModel.lookupWord(text)
                    }
                    mode?.finish()
                }
                return true
            }
            override fun onDestroyActionMode(mode: ActionMode?) {
                // Nothing to release: the menu holds no state beyond the action mode itself.
            }
        }
    }
}

/** Hosts the Readium EPUB navigator fragment, replacing any fragment left over from a previous book. */
@OptIn(ExperimentalReadiumApi::class)
@Composable
private fun EpubNavigatorHost(
    activity: AppCompatActivity,
    publication: Publication,
    viewModel: ReaderViewModel,
    selectionCallback: ActionMode.Callback,
    navListener: EpubNavigatorFragment.Listener,
    onFragmentCreated: (EpubNavigatorFragment) -> Unit,
) {
    val initialLocatorState by viewModel.initialLocator.collectAsState()
    AndroidView(
        factory = { context ->
            FrameLayout(context).apply {
                id = View.generateViewId()
                val fm = activity.supportFragmentManager
                val existing = fm.findFragmentByTag(EPUB_READER_FRAGMENT)
                if (existing != null) fm.beginTransaction().remove(existing).commitNow()

                val factory = EpubNavigatorFactory(publication)
                val fragment = factory.createFragmentFactory(
                    initialLocator = initialLocatorState,
                    configuration = EpubNavigatorFragment.Configuration().apply {
                        selectionActionModeCallback = selectionCallback
                    },
                    listener = navListener
                ).instantiate(activity.classLoader, EpubNavigatorFragment::class.java.name) as EpubNavigatorFragment

                onFragmentCreated(fragment)
                fm.beginTransaction().replace(this.id, fragment, EPUB_READER_FRAGMENT).commit()
            }
        },
        modifier = Modifier.fillMaxSize()
    )
}

/** The panels and dialogs drawn over the page: error, find, highlights, bookmarks, contents, settings, dictionary, tags. */
@Composable
private fun ReaderPanels(viewModel: ReaderViewModel) {
    val hasChapterError by viewModel.hasChapterError.collectAsState()
    val showSearch by viewModel.showSearch.collectAsState()
    val showHighlights by viewModel.showHighlights.collectAsState()
    val showBookmarks by viewModel.showBookmarks.collectAsState()
    val showToc by viewModel.showToc.collectAsState()
    val showReaderSettings by viewModel.showReaderSettings.collectAsState()
    val showFullSettings by viewModel.showFullSettings.collectAsState()
    val epubPreferences by viewModel.epubPreferences.collectAsState()
    val squareish = rememberReaderLayoutProfile().isSquareish
    val isBottomSheetVisible by viewModel.isBottomSheetVisible.collectAsState()
    val showTagSelector by viewModel.showTagSelector.collectAsState()

    if (hasChapterError) {
        ChapterErrorOverlay(
            onReturnToLibrary = { viewModel.dismissChapterErrorAndClose() }
        )
    }

    if (showSearch) SearchPanel(viewModel)
    if (showHighlights) HighlightsPanel(viewModel)
    if (showBookmarks) BookmarksPanel(viewModel)
    if (showToc) TocPanel(viewModel)

    if (showReaderSettings && squareish && !showFullSettings) {
        ReaderAppearanceStrip(
            prefs = epubPreferences,
            onChange = { viewModel.savePreferences(it) },
            onDone = { viewModel.dismissReaderSettings() },
            onAllSettings = { viewModel.showFullReaderSettings() },
        )
    } else if (showReaderSettings) {
        Surface(modifier = Modifier.fillMaxSize().zIndex(10f).tid(Tid.Screen.readerSettings)) {
            ReaderSettingsSheet(viewModel = viewModel, onDismiss = { viewModel.dismissReaderSettings() })
        }
    }

    if (isBottomSheetVisible) DictionaryPanel(viewModel)

    if (showTagSelector) {
        TagSelectorDialog(onTagSelected = { tag -> viewModel.saveHighlightWithTag(tag) }, onDismiss = { viewModel.dismissTagSelector() })
    }
}

@Composable
private fun SearchPanel(viewModel: ReaderViewModel) {
    val bookSearchQuery by viewModel.bookSearchQuery.collectAsState()
    val searchResults by viewModel.searchResults.collectAsState()
    val isBookSearching by viewModel.isBookSearching.collectAsState()
    Surface(modifier = Modifier.fillMaxSize().zIndex(15f).tid(Tid.Screen.readerSearch)) {
        BookSearchOverlay(query = bookSearchQuery, results = searchResults, isSearching = isBookSearching, onQueryChange = { viewModel.searchInBook(it) }, onSearch = { viewModel.searchInBook(it) }, onResultClick = { viewModel.onSearchResultClicked(it) }, onDismiss = { viewModel.toggleSearch() })
    }
}

@Composable
private fun HighlightsPanel(viewModel: ReaderViewModel) {
    val highlightsList by viewModel.bookmarksList.collectAsState()
    Surface(modifier = Modifier.fillMaxSize().zIndex(15f)) {
        BookHighlightsOverlay(highlights = highlightsList, onHighlightClick = { viewModel.onHighlightClicked(it) }, onDismiss = { viewModel.toggleHighlights() })
    }
}

@Composable
private fun BookmarksPanel(viewModel: ReaderViewModel) {
    val savedBookmarks by viewModel.savedBookmarks.collectAsState()
    Surface(modifier = Modifier.fillMaxSize().zIndex(15f).tid(Tid.Screen.bookmarks)) {
        BookBookmarksOverlay(bookmarks = savedBookmarks, onBookmarkClick = { viewModel.onBookmarkClicked(it) }, onDismiss = { viewModel.toggleBookmarksList() })
    }
}

@Composable
private fun TocPanel(viewModel: ReaderViewModel) {
    val publication by viewModel.publication.collectAsState()
    val currentLocator by viewModel.currentLocator.collectAsState()
    val toc = publication?.tableOfContents ?: return
    Surface(modifier = Modifier.fillMaxSize().zIndex(15f)) {
        TableOfContents(toc = toc, currentHref = currentLocator?.href?.toString(), onLinkSelected = { link -> viewModel.onTocItemSelected(link) }, onDismiss = { viewModel.toggleToc() })
    }
}

@Composable
private fun DictionaryPanel(viewModel: ReaderViewModel) {
    val dictionaryResponse by viewModel.dictionaryResponse.collectAsState()
    val isDictionaryLoading by viewModel.isDictionaryLoading.collectAsState()
    DictionaryBottomSheet(responseText = dictionaryResponse, isLoading = isDictionaryLoading, onDismiss = { viewModel.dismissBottomSheet() })
}

@Composable
private fun DeleteHighlightDialog(onConfirm: () -> Unit, onDismiss: () -> Unit) {
    Bb7ConfirmDialog(
        message = "Delete this highlight? This action cannot be undone.",
        confirmLabel = "Delete",
        onConfirm = onConfirm,
        onDismiss = onDismiss,
        modifier = Modifier.zIndex(4f),
    )
}

/** Read aloud could not start: say why, and offer the Android voice settings where voices are added. */
@Composable
private fun TtsProblemDialog(message: String, onDismiss: () -> Unit) {
    val context = androidx.compose.ui.platform.LocalContext.current
    androidx.compose.material3.AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Read aloud") },
        text = { Text(message) },
        confirmButton = {
            androidx.compose.material3.TextButton(onClick = {
                runCatching {
                    context.startActivity(
                        android.content.Intent("com.android.settings.TTS_SETTINGS")
                            .addFlags(android.content.Intent.FLAG_ACTIVITY_NEW_TASK),
                    )
                }
                onDismiss()
            }) { Text("Voice settings") }
        },
        dismissButton = { androidx.compose.material3.TextButton(onClick = onDismiss) { Text("Close") } },
    )
}

/** The key list (K): everything the keyboard does in a book, on one screen. */
@Composable
private fun ReaderKeyListDialog(onDismiss: () -> Unit) {
    androidx.compose.material3.AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Keys") },
        text = {
            Text(
                "← →  turn page\n" +
                    "Space / Shift+Space  forward / back\n" +
                    "Enter  show or hide the bars\n" +
                    "B  bookmark    T  contents    F  find\n" +
                    "A  look (font, size, spacing)\n" +
                    "S / Shift+S  text bigger / smaller\n" +
                    "M  switch theme    V  read aloud\n" +
                    ",  .  sentence back / forward    -  +  speech speed\n" +
                    "H  highlight    D  look up    N / Shift+N  find result\n" +
                    "L  highlights    Shift+L  bookmarks\n" +
                    "K  this list\n" +
                    "Esc  close, then leave",
            )
        },
        confirmButton = { androidx.compose.material3.TextButton(onClick = onDismiss) { Text("Close") } },
    )
}

/** The floating read-aloud controls and their settings sheet, shown while the TTS bar is up. */
@OptIn(ExperimentalReadiumApi::class)
@Composable
private fun ReaderTtsControls(viewModel: ReaderViewModel) {
    val showTtsBar by viewModel.showTtsBar.collectAsState()
    if (!showTtsBar) return
    val isTtsActive by viewModel.isTtsActive.collectAsState()
    val ttsSettings by viewModel.ttsSettings.collectAsState()
    val showTtsSettingsSheet by viewModel.showTtsSettingsSheet.collectAsState()
    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(end = 16.dp)
            .zIndex(20f),
        contentAlignment = Alignment.CenterEnd
    ) {
        TtsVerticalFloatingPill(
            isVisible = true,
            isPlaying = isTtsActive,
            onPlayPause = { viewModel.toggleTts() },
            onStop = { viewModel.closeTts() },
            onSkipForward = { viewModel.skipForward() },
            onSkipBackward = { viewModel.skipBackward() },
            onOpenSettings = { viewModel.openTtsSettings() }
        )
        TtsSettingsBottomSheet(
            isVisible = showTtsSettingsSheet,
            onDismiss = { viewModel.closeTtsSettings() },
            ttsSettings = ttsSettings,
            onSpeedChange = { speed: Float ->
                viewModel.updateTtsSpeed(speed)
            },
            onPitchChange = { pitch: Float ->
                viewModel.updateTtsPitch(pitch)
            },
            onStyleChange = { style: String ->
                viewModel.setTtsVisualStyle(style)
            },
            onAutoPageTurnChange = { enabled: Boolean ->
                viewModel.setTtsAutoPageTurn(enabled)
            },
            viewModel = viewModel
        )
    }
}

// Helper Composable for Bookmarks List
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BookBookmarksOverlay(
    bookmarks: List<HighlightEntity>,
    onBookmarkClick: (HighlightEntity) -> Unit,
    onDismiss: () -> Unit,
) {
    val containerColor = MaterialTheme.colorScheme.surface
    val contentColor = MaterialTheme.colorScheme.onSurface

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        "Bookmarks",
                        fontWeight = FontWeight.Bold
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onDismiss, modifier = Modifier.tid(Tid.Bookmarks.CLOSE)) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Close Bookmarks",
                            tint = contentColor
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = containerColor,
                    titleContentColor = contentColor,
                    navigationIconContentColor = contentColor
                )
            )
        },
        containerColor = containerColor
    ) { padding ->
        Column(modifier = Modifier.padding(padding).fillMaxSize()) {
            if (bookmarks.isEmpty()) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text("No bookmarks added yet.", color = Color.Gray)
                }
            } else {
                LazyColumn(modifier = Modifier.padding(16.dp)) {
                    items(bookmarks.size) { i ->
                        val bookmark = bookmarks[i]
                        // --- MAESTRO FIX 2: Added mergeDescendants to Bookmark items ---
                        Card(
                            onClick = { onBookmarkClick(bookmark) },
                            modifier = if (i == 0) {
                                Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 4.dp)
                                    .tids(Tid.Bookmarks.item(bookmark.id), Tid.Bookmarks.FIRST)
                                    .semantics(mergeDescendants = true) {
                                        contentDescription = "Bookmark: ${bookmark.text}"
                                    }
                            } else {
                                Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 4.dp)
                                    .tid(Tid.Bookmarks.item(bookmark.id))
                                    .semantics(mergeDescendants = true) {
                                        contentDescription = "Bookmark: ${bookmark.text}"
                                    }
                            },
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
                        ) {
                            Column(modifier = Modifier.padding(16.dp)) {
                                Text(
                                    text = bookmark.text,
                                    style = MaterialTheme.typography.bodyLarge,
                                    fontWeight = FontWeight.Bold,
                                    color = contentColor
                                )
                            }
                        }
                        if (i < bookmarks.size - 1) {
                            HorizontalDivider(thickness = 0.5.dp, color = Color.Gray.copy(alpha = 0.5f))
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun TagSelectorDialog(
    onTagSelected: (String) -> Unit,
    onDismiss: () -> Unit
) {
    val tags = listOf("General", "Research", "Quotes", "Characters")
    val focus = remember { androidx.compose.ui.focus.FocusRequester() }
    androidx.compose.runtime.LaunchedEffect(Unit) { runCatching { focus.requestFocus() } }
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Save Highlight As...") },
        text = {
            // The first letter of each tag saves the highlight with it: G, R, Q or C.
            Column(
                verticalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier
                    .focusRequester(focus)
                    .focusable()
                    .onPreviewKeyEvent { event ->
                        if (event.type != KeyEventType.KeyDown) return@onPreviewKeyEvent false
                        val letter = event.utf16CodePoint.toChar().uppercaseChar()
                        val tag = tags.firstOrNull { it.first() == letter } ?: return@onPreviewKeyEvent false
                        onTagSelected(tag)
                        true
                    },
            ) {
                tags.forEach { tag ->
                    // --- MAESTRO FIX 3: Explicit description for the tags ---
                    OutlinedButton(
                        onClick = { onTagSelected(tag) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .semantics(mergeDescendants = true) {
                                contentDescription = "Select tag: $tag"
                            },
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = MaterialTheme.colorScheme.onSurface)
                    ) { Text("$tag  (${tag.first()})") }
                }
            }
        },
        confirmButton = {},
        dismissButton = { TextButton(onClick = onDismiss) { Text("Cancel") } },
        modifier = Modifier.zIndex(5f)
    )
}

@Composable
fun TtsVerticalFloatingPill(
    isVisible: Boolean,
    isPlaying: Boolean,
    onPlayPause: () -> Unit,
    onStop: () -> Unit,
    onSkipForward: () -> Unit,
    onSkipBackward: () -> Unit,
    onOpenSettings: () -> Unit
) {
    if (!isVisible) return

    val backgroundColor = MaterialTheme.colorScheme.surfaceVariant
    val contentColor = MaterialTheme.colorScheme.onSurfaceVariant

    var offsetX by remember { mutableStateOf(0f) }
    var offsetY by remember { mutableStateOf(0f) }

    Surface(
        modifier = Modifier
            .tid(Tid.Tts.pill)
            .offset { IntOffset(offsetX.roundToInt(), offsetY.roundToInt()) }
            .pointerInput(Unit) {
                detectDragGestures { change, dragAmount ->
                    change.consume()
                    offsetX += dragAmount.x
                    offsetY += dragAmount.y
                }
            }
            .width(56.dp)
            .wrapContentHeight(),
        shape = RoundedCornerShape(28.dp),
        color = backgroundColor,
        shadowElevation = 8.dp,
        border = null
    ) {
        Column(
            modifier = Modifier.padding(vertical = 8.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            IconButton(onClick = onStop, modifier = Modifier.tid(Tid.Tts.stop)) { Icon(Icons.Default.Close, "Exit TTS", tint = contentColor) }

            Spacer(modifier = Modifier.height(1.dp).width(24.dp).background(Color.Gray.copy(alpha = 0.5f)))

            IconButton(onClick = onSkipBackward, modifier = Modifier.tid(Tid.Tts.prev)) { Icon(Icons.Default.SkipPrevious, "Previous Sentence", tint = contentColor) }

            FilledIconButton(
                onClick = onPlayPause,
                modifier = Modifier.size(48.dp).tid(Tid.Tts.playPause),
                colors = IconButtonDefaults.filledIconButtonColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    contentColor = MaterialTheme.colorScheme.onPrimary
                )
            ) {
                // --- MAESTRO FIX 4: Dynamic State-Aware Descriptions ---
                Icon(
                    imageVector = if (isPlaying) Icons.Default.Pause else Icons.Default.PlayArrow,
                    contentDescription = if (isPlaying) "Pause TTS" else "Play TTS"
                )
            }

            IconButton(onClick = onSkipForward, modifier = Modifier.tid(Tid.Tts.next)) { Icon(Icons.Default.SkipNext, "Next Sentence", tint = contentColor) }

            Spacer(modifier = Modifier.height(1.dp).width(24.dp).background(Color.Gray.copy(alpha = 0.5f)))

            IconButton(onClick = onOpenSettings, modifier = Modifier.tid(Tid.Tts.settings)) { Icon(Icons.Default.Tune, "TTS Settings", tint = contentColor) }
        }
    }
}

@Composable
fun ChapterErrorOverlay(
    onReturnToLibrary: () -> Unit
) {
    val backgroundColor = MaterialTheme.colorScheme.background
    val contentColor = MaterialTheme.colorScheme.onBackground

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(backgroundColor)
            .padding(32.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            Icon(
                imageVector = Icons.Default.WarningAmber,
                contentDescription = "Formatting Error",
                modifier = Modifier.size(64.dp),
                tint = contentColor
            )

            Text(
                text = "Chapter Formatting Error",
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold,
                color = contentColor
            )

            Text(
                text = "This specific section of the book contains structural formatting errors (like a missing closing tag) and cannot be rendered by the reading engine.",
                style = MaterialTheme.typography.bodyLarge,
                color = contentColor,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(16.dp))

            // --- MAESTRO FIX 5: Explicit semantics for Error Exit Button ---
            OutlinedButton(
                onClick = onReturnToLibrary,
                modifier = Modifier
                    .fillMaxWidth(0.8f)
                    .height(56.dp)
                    .semantics(mergeDescendants = true) {
                        contentDescription = "Return to Library"
                    },
                shape = RoundedCornerShape(8.dp),
                colors = ButtonDefaults.outlinedButtonColors(
                    contentColor = contentColor
                ),
                border = BorderStroke(2.dp, contentColor)
            ) {
                Text("Return to Library", style = MaterialTheme.typography.titleMedium)
            }
        }
    }
}