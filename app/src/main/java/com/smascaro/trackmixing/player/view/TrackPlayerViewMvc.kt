package com.smascaro.trackmixing.player.view

import com.smascaro.trackmixing.base.time.Seconds
import com.smascaro.trackmixing.base.ui.architecture.view.ObservableViewMvc
import com.smascaro.trackmixing.playback.model.TrackInstrument
import com.smascaro.trackmixing.playback.model.TrackVolumeBundle
import com.smascaro.trackmixing.player.model.TrackPlayerData

interface TrackPlayerViewMvc :
    ObservableViewMvc<TrackPlayerViewMvc.Listener> {
    interface Listener {
        fun onLayoutClick()
        fun onActionButtonClicked()
        fun onPlayerStateChanged()
        fun onServiceRunningCheck(running: Boolean)
        fun onPlayerSwipedOut()
        fun onSeekRequestEvent(progress: Int)
        fun onTrackVolumeChanged(
            trackInstrument: TrackInstrument,
            volume: Int
        )
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
