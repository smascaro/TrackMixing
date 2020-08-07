package com.smascaro.trackmixing.ui.player

import com.smascaro.trackmixing.tracks.Track
import timber.log.Timber

class TracksPlayerController(private var mTrack: Track) :
    TracksPlayerViewMvc.Listener {

    private lateinit var mViewMvc: TracksPlayerViewMvc

    fun bindView(viewMvc: TracksPlayerViewMvc) {
        mViewMvc = viewMvc
        mViewMvc.registerListener(this)
    }

    fun loadTrack() {
        mViewMvc.loadTrack(mTrack)
    }

    fun togglePlayPause() {

    }

    fun onDestroy() {
        mViewMvc.unregisterListener(this)
        mViewMvc.onDestroy()
    }

    fun onCreate() {
        mViewMvc.startService()
        loadTrack()
    }

    fun playMaster() {
        if (mViewMvc.isServiceConnected()) {
            mViewMvc.playMaster()
        } else {
            Timber.w("Can't play master because service is not connected yet.")
        }
    }

    override fun onServiceConnected() {
        loadTrack()
    }
}