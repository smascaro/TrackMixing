package com.smascaro.trackmixing.ui.common.navigationhelper

import androidx.navigation.NavController
import androidx.navigation.fragment.FragmentNavigator
import com.smascaro.trackmixing.R
import com.smascaro.trackmixing.tracks.Track
import com.smascaro.trackmixing.ui.details.TrackDetailsFragmentDirections
import com.smascaro.trackmixing.ui.search.SongSearchFragmentDirections
import com.smascaro.trackmixing.ui.trackslist.TracksListFragmentDirections

class NavigationHelper(private val mNavController: NavController) {
//    private lateinit var mNavController: NavController

//    fun bindNavController(navController: NavController) {
//        mNavController = navController
//    }

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

    fun toPlayer(path: String) {
        val action = when (mNavController.currentDestination?.id) {
            R.id.destination_search -> SongSearchFragmentDirections.actionDestinationSearchToDestinationPlayer(
                path
            )
            R.id.destination_tracks_list -> TracksListFragmentDirections.actionDestinationTracksListToDestinationPlayer(
                path
            )
            R.id.destination_track_details -> TrackDetailsFragmentDirections.actionDestinationTrackDetailsToDestinationPlayer(
                path
            )
            else -> null
        }
        if (action != null) {
            mNavController.navigate(action)
        }
    }
}