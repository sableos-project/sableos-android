package org.sableos.reader.ui.comic

import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.platform.LocalDensity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import org.sableos.reader.comic.ComicFit
import org.sableos.reader.comic.ComicKeyCommand
import org.sableos.reader.comic.ComicKeyPolicy
import org.sableos.reader.comic.ComicZoom
import org.sableos.reader.input.LocalEditableFocus
import org.sableos.reader.input.LocalReaderKeyDispatcher

private const val LINE_SCROLL_DP = 64f

/** Registers the comic key handler with the Activity-level dispatcher for as long as the screen is composed. */
@Composable
internal fun ComicKeyboard(viewModel: ComicReaderViewModel, scroll: ScrollTarget, onLeave: () -> Unit) {
    val dispatcher = LocalReaderKeyDispatcher.current
    val editable = LocalEditableFocus.current
    val scope = rememberCoroutineScope()
    scroll.linePx = LINE_SCROLL_DP * LocalDensity.current.density
    val latestLeave = rememberUpdatedState(onLeave)
    DisposableEffect(dispatcher, viewModel, scroll) {
        val unregister = dispatcher.register { input ->
            val state = viewModel.uiState.value
            val overlayOpen = state.overlay != ComicOverlay.NONE
            val pageScrolls = state.settings.effectiveFit == ComicFit.FIT_WIDTH
            val zoomed = state.zoomPercent > ComicZoom.MIN
            val command = ComicKeyPolicy.decide(
                input, state.settings.mode, editable.focused, overlayOpen, pageScrolls, zoomed,
            )
            command?.let {
                perform(it, viewModel, scroll, latestLeave.value, scope)
            }
            command != null
        }
        onDispose(unregister)
    }
}

private fun perform(
    command: ComicKeyCommand,
    viewModel: ComicReaderViewModel,
    scroll: ScrollTarget,
    onLeave: () -> Unit,
    scope: CoroutineScope,
) {
    val scrollBy = { delta: Float -> scope.launch { scroll.scrollBy?.invoke(delta) }; Unit }
    val panBy = { delta: Float -> scope.launch { scroll.panBy?.invoke(delta) }; Unit }
    when (command) {
        ComicKeyCommand.ADVANCE_IN_PAGE -> when {
            scroll.canScrollDown?.invoke() == true -> scrollBy(scroll.page(1f))
            else -> viewModel.onAction(ComicUiAction.NextPage)
        }
        ComicKeyCommand.REVERSE_IN_PAGE -> when {
            scroll.canScrollUp?.invoke() == true -> scrollBy(scroll.page(-1f))
            else -> viewModel.onAction(ComicUiAction.PreviousPage)
        }
        ComicKeyCommand.PAN_LEFT -> panBy(scroll.line(-1f))
        ComicKeyCommand.PAN_RIGHT -> panBy(scroll.line(1f))
        ComicKeyCommand.ZOOM_IN -> viewModel.onAction(ComicUiAction.ZoomIn)
        ComicKeyCommand.ZOOM_OUT -> viewModel.onAction(ComicUiAction.ZoomOut)
        ComicKeyCommand.ZOOM_RESET -> viewModel.onAction(ComicUiAction.ZoomReset)
        ComicKeyCommand.NEXT_PAGE -> viewModel.onAction(ComicUiAction.NextPage)
        ComicKeyCommand.PREVIOUS_PAGE -> viewModel.onAction(ComicUiAction.PreviousPage)
        ComicKeyCommand.SCROLL_LINE_DOWN -> scrollBy(scroll.line(1f))
        ComicKeyCommand.SCROLL_LINE_UP -> scrollBy(scroll.line(-1f))
        ComicKeyCommand.SCROLL_PAGE_DOWN -> scrollBy(scroll.page(1f))
        ComicKeyCommand.SCROLL_PAGE_UP -> scrollBy(scroll.page(-1f))
        ComicKeyCommand.TOGGLE_BOOKMARK -> viewModel.onAction(ComicUiAction.ToggleBookmark)
        ComicKeyCommand.OPEN_CONTENTS -> viewModel.onAction(ComicUiAction.Show(ComicOverlay.CONTENTS))
        ComicKeyCommand.OPEN_APPEARANCE -> viewModel.onAction(ComicUiAction.Show(ComicOverlay.MODE))
        ComicKeyCommand.TOGGLE_CHROME -> viewModel.onAction(ComicUiAction.ToggleChrome)
        ComicKeyCommand.ESCAPE -> if (!viewModel.dismissTransientSurface()) onLeave()
    }
}
