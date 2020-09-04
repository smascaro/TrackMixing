package com.smascaro.trackmixing.player.view

import com.smascaro.trackmixing.common.utils.TrackVolumeBundle
import com.smascaro.trackmixing.common.view.architecture.ObservableViewMvc
import com.smascaro.trackmixing.playbackservice.model.TrackInstrument

interface TracksPlayerViewMvc :
    ObservableViewMvc<TracksPlayerViewMvc.Listener> {
    interface Listener {
        fun onServiceConnected()
        fun onTrackVolumeChanged(trackInstrument: TrackInstrument, volume: Int)
        fun onActionButtonClicked()
    }

    fun isServiceStarted(): Boolean
    fun setCurrentVolume(trackInstrument: TrackInstrument, volume: Int)
    fun showPlayButton()
    fun showPauseButton()
    fun onDestroy()
    fun bindVolumes(volumes: TrackVolumeBundle)
    fun updateTimestamp(timestamp: Int)
}