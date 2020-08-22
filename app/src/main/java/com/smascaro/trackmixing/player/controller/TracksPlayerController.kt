package com.smascaro.trackmixing.player.controller

import com.smascaro.trackmixing.common.controller.BaseController
import com.smascaro.trackmixing.common.data.model.Track
import com.smascaro.trackmixing.common.error.NoLoadedTrackException
import com.smascaro.trackmixing.player.view.TracksPlayerViewMvc
import timber.log.Timber
import javax.inject.Inject

class TracksPlayerController @Inject constructor() :
    BaseController<TracksPlayerViewMvc>(),
    TracksPlayerViewMvc.Listener {

    private lateinit var mTrack: Track

    fun bindTrack(track: Track) {
        mTrack = track
    }

    fun loadTrack() {
        if (!this::mTrack.isInitialized) {
            throw NoLoadedTrackException()
        }
        viewMvc.loadTrack(mTrack)
    }

    fun onDestroy() {
        viewMvc.unregisterListener(this)
        viewMvc.onDestroy()
    }

    fun onCreate() {
        ensureViewMvcBound()
        loadTrack()
        viewMvc.registerListener(this)
    }

    fun playMaster() {
        if (viewMvc.isServiceStarted()) {
            viewMvc.playMaster()
        } else {
            Timber.w("Can't play master because service is not connected yet.")
        }
    }

    override fun onServiceConnected() {
        loadTrack()
    }
}