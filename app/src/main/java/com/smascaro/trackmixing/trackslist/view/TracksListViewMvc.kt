package com.smascaro.trackmixing.trackslist.view

import com.smascaro.trackmixing.common.data.model.Track
import com.smascaro.trackmixing.common.view.architecture.ObservableViewMvc


interface TracksListViewMvc :
    ObservableViewMvc<TracksListViewMvc.Listener> {
    interface Listener {
        fun onTrackClicked(track: Track)
        fun onCurrentDataSourceRequest(dataSource: TracksDataSource)
    }

    enum class TracksDataSource {
        SERVER, DATABASE
    }

    fun bindTracks(tracks: List<Track>)
    fun navigateToPlayer(track: Track)
    fun displayFloatingCard()
    fun showDetails(track: Track)
    fun getCurrentDataSource(): TracksDataSource
}