package com.smascaro.trackmixing.common.utils.navigation

import androidx.navigation.NavController
import com.smascaro.trackmixing.common.data.model.Track
import com.smascaro.trackmixing.common.view.architecture.Observable
import com.smascaro.trackmixing.settings.business.downloadtestdata.selection.model.TestDataBundleInfo

interface NavigationHelper : Observable<NavigationHelper.Listener> {
    interface Listener {
        fun onDestinationChange(destination: NavigationDestination)
    }

    fun navigateTo(destination: Int)
    fun bindNavController(navController: NavController)
    fun toPlayer(track: Track)
    fun toSearch()
    fun back(): Boolean
    fun backAndPop(): Boolean
    fun toSettings()
    fun toTestDataDownload(data: List<TestDataBundleInfo>)
}