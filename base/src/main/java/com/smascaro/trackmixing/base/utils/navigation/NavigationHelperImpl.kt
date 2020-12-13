package com.smascaro.trackmixing.base.utils.navigation

import androidx.navigation.NavController
import androidx.navigation.NavDestination
import androidx.navigation.NavDirections
import javax.inject.Inject

class NavigationHelperImpl @Inject constructor() : NavigationHelper {
    private var navController: NavController? = null
    override fun navigate(action: NavDirections) {
        navController?.navigate(action)
    }

    override fun bindNavController(navController: NavController) {
        this.navController = navController
    }

    override val currentDestination: NavDestination?
        get() = navController?.currentDestination

    override fun back(): Boolean {
        return navController?.navigateUp() ?: false
    }

    override fun backAndPop(): Boolean {
        return navController?.popBackStack() ?: false
    }
}