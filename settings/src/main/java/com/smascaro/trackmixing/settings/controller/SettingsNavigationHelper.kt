package com.smascaro.trackmixing.settings.controller

import androidx.navigation.NavController
import androidx.navigation.NavDirections
import com.smascaro.trackmixing.base.utils.navigation.NavigationHelper
import javax.inject.Inject

class SettingsNavigationHelper @Inject constructor() : NavigationHelper {
    private var navController: NavController? = null
    override fun navigate(action:NavDirections) {
        navController?.navigate(action)
    }

    override fun bindNavController(navController: NavController) {
        this.navController = navController
    }

    override fun back(): Boolean {
        return navController?.navigateUp() ?: false
    }

    override fun backAndPop(): Boolean {
        return navController?.popBackStack() ?: false
    }
}