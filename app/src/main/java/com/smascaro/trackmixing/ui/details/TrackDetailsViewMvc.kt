package com.smascaro.trackmixing.ui.details

import com.smascaro.trackmixing.tracks.Track
import com.smascaro.trackmixing.ui.common.BaseObservableViewMvc
import com.smascaro.trackmixing.ui.common.ObservableViewMvc

interface TrackDetailsViewMvc : ObservableViewMvc<TrackDetailsViewMvc.Listener> {

    interface Listener {
        fun onGoToPlayerButtonClicked(track: Track)
    }

    fun initUI()

}
