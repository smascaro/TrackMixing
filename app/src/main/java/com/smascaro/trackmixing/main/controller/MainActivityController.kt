package com.smascaro.trackmixing.main.controller

import com.smascaro.trackmixing.base.coroutine.IoCoroutineScope
import com.smascaro.trackmixing.base.coroutine.MainCoroutineScope
import com.smascaro.trackmixing.base.ui.architecture.controller.BaseController
import com.smascaro.trackmixing.main.view.MainActivityViewMvc
import com.smascaro.trackmixing.playback.utils.media.PlaybackSession
import com.smascaro.trackmixing.playback.utils.state.PlaybackStateManager
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

class MainActivityController @Inject constructor(
    private val playbackSession: PlaybackSession,
    private val ui: com.smascaro.trackmixing.base.coroutine.MainCoroutineScope,
    private val io: com.smascaro.trackmixing.base.coroutine.IoCoroutineScope
) :
    BaseController<MainActivityViewMvc>(), MainActivityViewMvc.Listener {
    fun onStart() {
        viewMvc.registerListener(this)
        updateBackgroundColor()
    }

    fun onStop() {
        viewMvc.unregisterListener(this)
    }

    override fun onPlayerStateChanged() {
        updateBackgroundColor()
    }

    private fun updateBackgroundColor() = ui.launch {
        val state = playbackSession.getState()
        if (state is PlaybackStateManager.PlaybackState.Stopped) {
            viewMvc.updateBackgroundColorToDefault()
        } else {
            val playingTrack =
                withContext(io.coroutineContext) { playbackSession.getTrack() }
            viewMvc.updateBackgroundColor(playingTrack.backgroundColor)
        }
    }

    override fun dispose() {
        viewMvc.unregisterListener(this)
    }
}