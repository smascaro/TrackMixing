package com.smascaro.trackmixing.common.utils

import androidx.navigation.NavController
import androidx.navigation.fragment.FragmentNavigator
import com.smascaro.trackmixing.common.data.model.Track

interface NavigationHelper {
    fun navigateTo(destination: Int)
    fun bindNavController(navController: NavController)
    fun toDetails(
        track: Track,
        extras: FragmentNavigator.Extras
    )

    fun toPlayer(track: Track)
    fun toSearch()
    fun back()
}