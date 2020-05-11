package com.smascaro.trackmixing.ui.trackslist

import com.smascaro.trackmixing.tracks.Track
import com.smascaro.trackmixing.ui.common.ObservableViewMvc


interface TracksListViewMvc : ObservableViewMvc<TracksListViewMvc.Listener> {
    interface Listener {
        fun onTrackClicked(track: Track)
    }

    fun bindTracks(tracks: List<Track>)
}