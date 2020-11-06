package com.smascaro.trackmixing.base.utils.navigation

import androidx.navigation.NavController
import androidx.navigation.NavDirections

interface NavigationHelper {
    fun navigate(action: NavDirections)
    fun bindNavController(navController: NavController)
    // fun toSearch()
    fun back(): Boolean
    fun backAndPop(): Boolean
    // fun toSettings()
    // fun toTestDataDownload(data: List<com.smascaro.trackmixing.settings.testdata.selection.model.TestDataBundleInfo>)
}