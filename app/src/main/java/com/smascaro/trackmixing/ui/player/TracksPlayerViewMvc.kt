package com.smascaro.trackmixing.ui.player

import com.smascaro.trackmixing.tracks.Track
import com.smascaro.trackmixing.ui.common.ObservableViewMvc

interface TracksPlayerViewMvc : ObservableViewMvc<TracksPlayerViewMvc.Listener> {
    interface Listener {
        fun onServiceConnected()
    }

    fun isServiceStarted(): Boolean
    fun startService()
    fun onDestroy()
    fun loadTrack(track: Track)
    fun playMaster()
    fun pauseMaster()
}