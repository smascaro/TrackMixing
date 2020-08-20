package com.smascaro.trackmixing.trackslist.view.listitem

import com.google.android.material.card.MaterialCardView
import com.smascaro.trackmixing.common.data.model.Track
import com.smascaro.trackmixing.common.view.architecture.ObservableViewMvc

interface TracksListItemViewMvc :
    ObservableViewMvc<TracksListItemViewMvc.Listener> {

    interface Listener {
        fun onTrackClicked(track: Track, card: MaterialCardView)
        fun onExpandOrCollapseDetailsRequest(itemPosition: Int)
    }

    fun bindTrack(track: Track)
    fun bindPosition(position: Int)
}