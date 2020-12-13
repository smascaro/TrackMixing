package com.smascaro.trackmixing.base.utils.navigation

import androidx.navigation.NavController
import androidx.navigation.NavDestination
import androidx.navigation.NavDirections

interface NavigationHelper {
    fun bindNavController(navController: NavController)
    val currentDestination: NavDestination?
    fun navigate(action: NavDirections)
    fun back(): Boolean
    fun backAndPop(): Boolean
}