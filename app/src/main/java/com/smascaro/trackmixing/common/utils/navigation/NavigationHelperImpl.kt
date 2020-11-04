package com.smascaro.trackmixing.common.utils.navigation

import androidx.navigation.NavController
import com.smascaro.trackmixing.R
import com.smascaro.trackmixing.settings.business.downloadtestdata.selection.model.TestDataBundleInfo
import com.smascaro.trackmixing.settings.business.downloadtestdata.selection.view.SelectTestDataFragmentDirections
import com.smascaro.trackmixing.trackslist.view.TracksListFragmentDirections
import javax.inject.Inject

class NavigationHelperImpl @Inject constructor() :
    NavigationHelper {
    private var mNavController: NavController? = null
    override fun navigateTo(destination: Int) {
        if (destination != mNavController?.currentDestination?.id) {
            mNavController?.navigate(destination)
        }
    }

    override fun bindNavController(navController: NavController) {
        mNavController = navController
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