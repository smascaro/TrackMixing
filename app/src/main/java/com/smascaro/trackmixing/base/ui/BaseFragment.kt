package com.smascaro.trackmixing.base.ui

import android.content.Context
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.smascaro.trackmixing.TrackMixingApplication
import com.smascaro.trackmixing.base.utils.navigation.NavigationHelper
import com.smascaro.trackmixing.di.component.SearchComponent
import com.smascaro.trackmixing.di.component.SettingsComponent
import javax.inject.Inject

abstract class BaseFragment : Fragment() {

    @Inject
    protected lateinit var navigationHelper: NavigationHelper

    override fun onAttach(context: Context) {
        super.onAttach(context)
        (requireContext().applicationContext as TrackMixingApplication).provideMainComponent().inject(this)
    }

    override fun onStart() {
        super.onStart()
        navigationHelper.bindNavController(findNavController())
    }
}

fun Fragment.getSearchComponent(): SearchComponent =
    (requireContext().applicationContext as TrackMixingApplication).provideSearchComponent()

fun Fragment.getSettingsComponent(): SettingsComponent =
    (requireContext().applicationContext as TrackMixingApplication).provideSettingsComponent()