package com.smascaro.trackmixing.player.view

import com.smascaro.trackmixing.common.view.architecture.ObservableViewMvc
import com.smascaro.trackmixing.playbackservice.model.TrackInstrument

interface TracksPlayerViewMvc :
    ObservableViewMvc<TracksPlayerViewMvc.Listener> {
    interface Listener {
        fun onServiceConnected()
        fun onTrackVolumeChanged(trackInstrument: TrackInstrument, volume: Int)
    }

    fun isServiceStarted(): Boolean
    fun onDestroy()
}