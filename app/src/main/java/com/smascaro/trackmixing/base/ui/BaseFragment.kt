package com.smascaro.trackmixing.base.ui

import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.smascaro.trackmixing.TrackMixingApplication
import com.smascaro.trackmixing.base.utils.navigation.NavigationHelper
import com.smascaro.trackmixing.di.component.SearchComponent
import javax.inject.Inject

abstract class BaseFragment : Fragment() {

    @Inject
    protected lateinit var navigationHelper: NavigationHelper

    override fun onStart() {
        super.onStart()
        navigationHelper.bindNavController(findNavController())
    }
}

fun Fragment.getSearchComponent(): SearchComponent =
    (requireContext().applicationContext as TrackMixingApplication).provideSearchComponent()