package com.smascaro.trackmixing.main.controller

import android.content.Intent
import com.smascaro.trackmixing.common.controller.BaseController
import com.smascaro.trackmixing.common.di.coroutines.IoCoroutineScope
import com.smascaro.trackmixing.common.di.coroutines.MainCoroutineScope
import com.smascaro.trackmixing.common.utils.PlaybackStateManager
import com.smascaro.trackmixing.main.view.MainActivityViewMvc
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import timber.log.Timber
import javax.inject.Inject

class MainActivityController @Inject constructor(
    private val playbackStateManager: PlaybackStateManager,
    private val ui: MainCoroutineScope,
    private val io: IoCoroutineScope
) :
    BaseController<MainActivityViewMvc>(), MainActivityViewMvc.Listener {
    fun onStart() {
        viewMvc.registerListener(this)
        updateBackgroundColor()
    }

    fun onStop() {
        viewMvc.unregisterListener(this)
    }

    fun handleIntent(intent: Intent) {
        if (intent.action == Intent.ACTION_SEND) {
            val url = if (intent.clipData != null && intent.clipData!!.itemCount > 0) {
                intent.clipData?.getItemAt(0)!!.text
            } else ""
            Timber.d(intent.toString())
            if (isYoutubeValidUrl(url)) {
                viewMvc.startProcessingRequest(url.toString())
            }
        }
    }

    fun isYoutubeValidUrl(url: CharSequence): Boolean =
        YoutubeUrlValidator(url.toString()).isValid()

    override fun onPlayerStateChanged() {
        updateBackgroundColor()
    }

    private fun updateBackgroundColor() = ui.launch {
        val state = playbackStateManager.getPlayingState()
        if (state is PlaybackStateManager.PlaybackState.Stopped) {
            viewMvc.updateBackgroundColorToDefault()
        } else {
            val playingTrack =
                withContext(io.coroutineContext) { playbackStateManager.getCurrentTrack() }
            viewMvc.updateBackgroundColor(playingTrack.backgroundColor)
        }
    }

    override fun dispose() {
        viewMvc.unregisterListener(this)
    }
}