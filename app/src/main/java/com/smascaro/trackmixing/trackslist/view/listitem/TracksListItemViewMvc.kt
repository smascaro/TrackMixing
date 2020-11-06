package com.smascaro.trackmixing.trackslist.view.listitem

import com.smascaro.trackmixing.base.data.model.Track
import com.smascaro.trackmixing.base.ui.architecture.view.ObservableViewMvc

interface TracksListItemViewMvc :
    ObservableViewMvc<TracksListItemViewMvc.Listener> {
    interface Listener {
        fun onTrackClicked(track: Track)
    }

    fun bindTrack(track: Track)
    fun bindPosition(position: Int)
}