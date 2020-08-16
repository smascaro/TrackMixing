package com.smascaro.trackmixing.ui.common

import androidx.fragment.app.Fragment
import androidx.navigation.NavController
import androidx.navigation.findNavController
import com.smascaro.trackmixing.R
import com.smascaro.trackmixing.common.TrackMixingApplication
import com.smascaro.trackmixing.common.di.ControllerCompositionRoot

open class BaseFragment : Fragment() {
    private var mControllerCompositionRoot: ControllerCompositionRoot? = null
    private lateinit var mNavigationController: NavController

    protected fun getCompositionRoot(): ControllerCompositionRoot {
        if (mControllerCompositionRoot == null) {
            return ControllerCompositionRoot(
                (requireActivity().application as TrackMixingApplication).getCompositionRoot(),
                requireActivity()
            )
        }
        return mControllerCompositionRoot!!
    }

    protected fun getNavigationController(): NavController {
        if (!this::mNavigationController.isInitialized) {
            mNavigationController = requireActivity().findNavController(R.id.nav_host_fragment)
        }
        return mNavigationController
    }
}