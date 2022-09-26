package com.smascaro.trackmixing.trackslist.view

import com.smascaro.trackmixing.base.data.model.Track
import com.smascaro.trackmixing.base.ui.architecture.view.ObservableViewMvc

interface TracksListViewMvc :
    ObservableViewMvc<TracksListViewMvc.Listener> {
    interface Listener {
        fun onTrackClicked(track: com.smascaro.trackmixing.base.data.model.Track)
        fun onSearchNavigationButtonClicked()
    }

    fun bindTracks(tracks: List<com.smascaro.trackmixing.base.data.model.Track>)
    fun refreshList()
}