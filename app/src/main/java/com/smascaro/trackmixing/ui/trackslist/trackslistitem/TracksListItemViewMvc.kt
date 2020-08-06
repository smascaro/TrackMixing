package com.smascaro.trackmixing.ui.trackslist.trackslistitem

import android.widget.LinearLayout
import com.google.android.material.card.MaterialCardView
import com.smascaro.trackmixing.tracks.Track
import com.smascaro.trackmixing.ui.common.ObservableViewMvc

interface TracksListItemViewMvc : ObservableViewMvc<TracksListItemViewMvc.Listener> {

    interface Listener {
        fun onTrackClicked(track: Track, card: MaterialCardView)
        fun onExpandOrCollapseDetailsRequest(itemPosition: Int)
    }

    fun bindTrack(track: Track)
    fun bindPosition(position: Int)
}