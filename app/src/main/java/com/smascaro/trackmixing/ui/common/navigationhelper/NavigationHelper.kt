package com.smascaro.trackmixing.ui.common.navigationhelper

import androidx.navigation.NavController
import androidx.navigation.fragment.FragmentNavigator
import com.smascaro.trackmixing.R
import com.smascaro.trackmixing.tracks.Track
import com.smascaro.trackmixing.ui.details.TrackDetailsFragmentDirections
import com.smascaro.trackmixing.ui.search.SongSearchFragmentDirections
import com.smascaro.trackmixing.ui.trackslist.TracksListFragmentDirections
import javax.inject.Inject

class NavigationHelper @Inject constructor(private val mNavController: NavController) {

    fun navigateTo(destination: Int) {
        if (destination != mNavController.currentDestination?.id) {
            mNavController.navigate(destination)
        }
    }

    fun toDetails(
        track: Track,
        extras: FragmentNavigator.Extras
    ) {
        val action =
            TracksListFragmentDirections.actionDestinationTracksListToDestinationTrackDetails(track)
        if (action != null) {
            mNavController.navigate(action, extras)
        }
    }

    fun toPlayer(track: Track) {
        val action = when (mNavController.currentDestination?.id) {
            R.id.destination_search -> SongSearchFragmentDirections.actionDestinationSearchToDestinationPlayer(
                track
            )
            R.id.destination_tracks_list -> TracksListFragmentDirections.actionDestinationTracksListToDestinationPlayer(
                track
            )
            R.id.destination_track_details -> TrackDetailsFragmentDirections.actionDestinationTrackDetailsToDestinationPlayer(
                track
            )
            else -> null
        }
        if (action != null) {
            mNavController.navigate(action)
        }
    }
}