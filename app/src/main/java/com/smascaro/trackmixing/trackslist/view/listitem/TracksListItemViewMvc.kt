package com.smascaro.trackmixing.trackslist.view.listitem

import com.smascaro.trackmixing.base.model.Track
import com.smascaro.trackmixing.common.view.architecture.ObservableViewMvc

interface TracksListItemViewMvc :
    ObservableViewMvc<TracksListItemViewMvc.Listener> {
    interface Listener {
        fun onTrackClicked(track: Track)
        fun onExpandOrCollapseDetailsRequest(itemPosition: Int)
    }

    fun bindTrack(track: Track)
    fun bindPosition(position: Int)
}