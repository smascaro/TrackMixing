package com.smascaro.trackmixing.ui.trackslist.trackslistitem

import com.smascaro.trackmixing.tracks.Track
import com.smascaro.trackmixing.ui.common.ObservableViewMvc

interface TracksListItemViewMvc : ObservableViewMvc<TracksListItemViewMvc.Listener> {

    interface Listener {
        fun onTrackClicked(track: Track)
    }

    fun bindTrack(track: Track)
}