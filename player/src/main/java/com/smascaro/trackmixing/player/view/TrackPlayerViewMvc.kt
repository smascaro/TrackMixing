package com.smascaro.trackmixing.player.view

import com.smascaro.trackmixing.base.time.Seconds
import com.smascaro.trackmixing.base.ui.architecture.view.ObservableViewMvc
import com.smascaro.trackmixing.playback.model.TrackVolumeBundle

interface TrackPlayerViewMvc :
    ObservableViewMvc<TrackPlayerViewMvc.Listener> {
    interface Listener {
        fun onActionButtonClicked()
        fun onPlayerStateChanged()
        fun onSeekRequestEvent(progress: Int)
        fun onTrackVolumeChanged(
            trackInstrument: com.smascaro.trackmixing.playback.model.TrackInstrument,
            volume: Int
        )
    }

    fun showPlayButton()
    fun showPauseButton()
    fun updateTimestamp(newTimestamp: Int, totalLength: Int)
    fun bindTrackDuration(length: Seconds)
    fun bindVolumes(volumes: TrackVolumeBundle)
    fun bindTrackTitle(title: String)
}
