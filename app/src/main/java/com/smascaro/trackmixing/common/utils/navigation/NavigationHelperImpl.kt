package com.smascaro.trackmixing.common.utils.navigation

import androidx.navigation.NavController
import com.smascaro.trackmixing.R
import com.smascaro.trackmixing.common.data.model.Track
import com.smascaro.trackmixing.common.view.architecture.BaseObservable
import com.smascaro.trackmixing.search.view.SongSearchFragmentDirections
import com.smascaro.trackmixing.settings.business.downloadtestdata.selection.model.TestDataBundleInfo
import com.smascaro.trackmixing.settings.business.downloadtestdata.selection.view.SelectTestDataFragmentDirections
import com.smascaro.trackmixing.trackslist.view.TracksListFragmentDirections
import javax.inject.Inject

class NavigationHelperImpl @Inject constructor() :
    BaseObservable<NavigationHelper.Listener>(),
    NavigationHelper {
    private var mNavController: NavController? = null
    override fun navigateTo(destination: Int) {
        if (destination != mNavController?.currentDestination?.id) {
            mNavController?.navigate(destination)
        }
    }

    override fun bindNavController(navController: NavController) {
        mNavController = navController
        mNavController?.addOnDestinationChangedListener { controller, navDestination, arguments ->
            var destination: NavigationDestination? = null
            destination = when (navDestination.id) {
                R.id.destination_search -> NavigationDestination.Search()
                R.id.destination_tracks_list -> NavigationDestination.TracksList()
                R.id.destination_player -> NavigationDestination.Player()
                R.id.destination_settings -> NavigationDestination.Settings()
                R.id.destination_select_test_data -> NavigationDestination.SelectTestData()
                R.id.destination_download_test_data -> NavigationDestination.DownloadTestData()
                else -> null
            }
            if (destination != null) {
                getListeners().forEach {
                    it.onDestinationChange(destination)
                }
            }
        }
    }

    override fun toPlayer(track: Track) {
        val action = when (mNavController?.currentDestination?.id) {
            R.id.destination_settings -> SongSearchFragmentDirections.actionDestinationSearchToDestinationPlayer(
                track
            )
            R.id.destination_tracks_list -> TracksListFragmentDirections.actionDestinationTracksListToDestinationPlayer(
                track
            )
            else -> null
        }
        if (action != null) {
            mNavController?.navigate(action)
        }
    }

    override fun toSearch() {
        val action = TracksListFragmentDirections.actionDestinationTracksListToDestinationSearch()
        mNavController?.navigate(action)
    }

    override fun back(): Boolean {
        return mNavController?.navigateUp() ?: false
    }

    override fun backAndPop(): Boolean {
        return mNavController?.popBackStack() ?: false
    }

    override fun toSettings() {
        val action = when (mNavController?.currentDestination?.id) {
            R.id.destination_tracks_list -> TracksListFragmentDirections.actionDestinationTracksListToDestinationSettings()
            else -> null
        }
        if (action != null) {
            mNavController?.navigate(action)
        }
    }

    override fun toTestDataDownload(data: List<TestDataBundleInfo>) {
        val action =
            SelectTestDataFragmentDirections.actionDestinationSelectTestDataToDownloadTestDataFragment(
                data.toTypedArray()
            )
        mNavController?.navigate(action)
    }
}