package com.smascaro.trackmixing.player.view

import com.smascaro.trackmixing.common.data.model.Track
import com.smascaro.trackmixing.common.view.architecture.ObservableViewMvc

interface TracksPlayerViewMvc :
    ObservableViewMvc<TracksPlayerViewMvc.Listener> {
    interface Listener {
        fun onServiceConnected()
    }

    fun isServiceStarted(): Boolean
    fun onDestroy()
    fun loadTrack(track: Track)
    fun playMaster()
    fun pauseMaster()
}