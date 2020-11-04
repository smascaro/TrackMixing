package com.smascaro.trackmixing.common.utils.navigation

import androidx.navigation.NavController
import com.smascaro.trackmixing.settings.business.downloadtestdata.selection.model.TestDataBundleInfo

interface NavigationHelper {
    fun navigateTo(destination: Int)
    fun bindNavController(navController: NavController)
    fun toSearch()
    fun back(): Boolean
    fun backAndPop(): Boolean
    fun toSettings()
    fun toTestDataDownload(data: List<TestDataBundleInfo>)
}