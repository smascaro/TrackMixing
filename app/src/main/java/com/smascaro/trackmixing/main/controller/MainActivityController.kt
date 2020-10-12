package com.smascaro.trackmixing.main.controller

import android.content.Intent
import com.smascaro.trackmixing.common.controller.BaseController
import com.smascaro.trackmixing.common.utils.PlaybackStateManager
import com.smascaro.trackmixing.main.view.MainActivityViewMvc
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

class MainActivityController @Inject constructor(
    private val playbackStateManager: PlaybackStateManager
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

    private fun updateBackgroundColor() {
        val state = playbackStateManager.getPlayingState()
        if (state is PlaybackStateManager.PlaybackState.Stopped) {
            viewMvc.updateBackgroundColorToDefault()
        } else {
            CoroutineScope(Dispatchers.Main).launch {
                val playingTrack = playbackStateManager.getCurrentTrack()
                viewMvc.updateBackgroundColor(playingTrack.backgroundColor)
            }
        }
    }

    override fun dispose() {
        viewMvc.unregisterListener(this)
    }
}