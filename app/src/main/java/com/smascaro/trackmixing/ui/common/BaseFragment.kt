package com.smascaro.trackmixing.ui.common

import com.smascaro.trackmixing.common.CustomApplication
import com.smascaro.trackmixing.common.di.CompositionRoot
import com.smascaro.trackmixing.common.di.ControllerCompositionRoot
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.navigation.NavController
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import com.smascaro.trackmixing.R

open class BaseFragment : Fragment() {
    private var mControllerCompositionRoot: ControllerCompositionRoot? = null
    private lateinit var mNavigationController: NavController

    protected fun getCompositionRoot(): ControllerCompositionRoot {
        if (mControllerCompositionRoot == null) {
            return ControllerCompositionRoot(
                (requireActivity().application as CustomApplication).getCompositionRoot(),
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