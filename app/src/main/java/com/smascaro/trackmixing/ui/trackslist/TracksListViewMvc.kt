package com.smascaro.trackmixing.ui.trackslist

import com.smascaro.trackmixing.tracks.Track
import com.smascaro.trackmixing.ui.common.ObservableViewMvc


interface TracksListViewMvc : ObservableViewMvc<TracksListViewMvc.Listener> {
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