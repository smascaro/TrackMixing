package com.smascaro.trackmixing.base.ui

import androidx.navigation.fragment.findNavController
import com.smascaro.trackmixing.base.utils.navigation.NavigationHelper
import dagger.android.support.DaggerFragment
import javax.inject.Inject

abstract class BaseFragment : DaggerFragment() {

    @Inject
    protected lateinit var navigationHelper: NavigationHelper

    override fun onStart() {
        super.onStart()
        navigationHelper.bindNavController(findNavController())
    }
}