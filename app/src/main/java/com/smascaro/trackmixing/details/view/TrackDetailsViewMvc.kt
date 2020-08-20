package com.smascaro.trackmixing.details.view

import com.smascaro.trackmixing.common.data.model.Track
import com.smascaro.trackmixing.common.view.architecture.ObservableViewMvc

interface TrackDetailsViewMvc :
    ObservableViewMvc<TrackDetailsViewMvc.Listener> {

    interface Listener {
        fun onGoToPlayerButtonClicked(track: Track)
    }

    fun bindTrack(track: Track)
    fun initUI()
}
