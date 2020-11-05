package com.smascaro.trackmixing.main.components.player.view

import com.smascaro.trackmixing.base.time.Seconds
import com.smascaro.trackmixing.common.utils.TrackVolumeBundle
import com.smascaro.trackmixing.common.view.architecture.ObservableViewMvc
import com.smascaro.trackmixing.main.components.player.model.TrackPlayerData
import com.smascaro.trackmixing.playbackservice.model.TrackInstrument

interface TrackPlayerViewMvc :
    ObservableViewMvc<TrackPlayerViewMvc.Listener> {
    interface Listener {
        fun onLayoutClick()
        fun onActionButtonClicked()
        fun onPlayerStateChanged()
        fun onServiceRunningCheck(running: Boolean)
        fun onPlayerSwipedOut()
        fun onSeekRequestEvent(progress: Int)
        fun onTrackVolumeChanged(trackInstrument: TrackInstrument, volume: Int)
    }

    fun onCreate()
    fun showPlayerBar(data: TrackPlayerData)
    fun showPlayButton()
    fun showPauseButton()
    fun updateTimestamp(newTimestamp: Int, totalLength: Int)
    fun bindTrackDuration(length: Seconds)
    fun bindVolumes(volumes: TrackVolumeBundle)
    fun openPlayer()
    fun onBackPressed(): Boolean
}
