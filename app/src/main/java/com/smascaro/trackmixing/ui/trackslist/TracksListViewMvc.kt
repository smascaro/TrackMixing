package com.smascaro.trackmixing.ui.trackslist

import com.smascaro.trackmixing.tracks.Track
import com.smascaro.trackmixing.ui.common.ObservableViewMvc
import com.smascaro.trackmixing.ui.common.navigationhelper.NavigationHelper


interface TracksListViewMvc : ObservableViewMvc<TracksListViewMvc.Listener> {
    interface Listener {
        fun onTrackClicked(track: Track)
    }

    fun bindNavigationHelper(navigationHelper: NavigationHelper)
    fun bindTracks(tracks: List<Track>)
    fun navigateToPlayer(path: String)
    fun displayFloatingCard()
}