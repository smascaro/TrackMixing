package com.smascaro.trackmixing.player.controller

import com.smascaro.trackmixing.common.error.NoLoadedTrackException
import com.smascaro.trackmixing.common.data.model.Track
import com.smascaro.trackmixing.player.view.TracksPlayerViewMvc
import timber.log.Timber
import javax.inject.Inject

class TracksPlayerController @Inject constructor() :
    TracksPlayerViewMvc.Listener {

    private lateinit var mViewMvc: TracksPlayerViewMvc
    private lateinit var mTrack: Track
    fun bindView(viewMvc: TracksPlayerViewMvc) {
        mViewMvc = viewMvc
        mViewMvc.registerListener(this)
    }

    fun bindTrack(track: Track) {
        mTrack = track
    }

    fun loadTrack() {
        if (!this::mTrack.isInitialized) {
            throw NoLoadedTrackException()
        }
        mViewMvc.loadTrack(mTrack)
    }

    fun onDestroy() {
        mViewMvc.unregisterListener(this)
        mViewMvc.onDestroy()
    }

    fun onCreate() {
        loadTrack()
    }

    fun playMaster() {
        if (mViewMvc.isServiceStarted()) {
            mViewMvc.playMaster()
        } else {
            Timber.w("Can't play master because service is not connected yet.")
        }
    }

    override fun onServiceConnected() {
        loadTrack()
    }
}