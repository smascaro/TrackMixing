package com.smascaro.trackmixing.ui.common.navigationhelper

import androidx.navigation.NavController
import com.smascaro.trackmixing.R
import com.smascaro.trackmixing.ui.search.SongSearchFragmentDirections
import com.smascaro.trackmixing.ui.trackslist.TracksListFragmentDirections

class NavigationHelper {
    private lateinit var mNavController: NavController

    fun bindNavController(navController: NavController) {
        mNavController = navController
    }

    fun navigateTo(destination: Int) {
        if (this::mNavController.isInitialized) {
            mNavController.navigate(destination)
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
            else -> null
        }
        if (action != null) {
            mNavController.navigate(action)
        }
    }
}