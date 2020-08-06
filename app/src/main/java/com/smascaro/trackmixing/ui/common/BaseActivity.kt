package com.smascaro.trackmixing.ui.common

import com.smascaro.trackmixing.common.TrackMixingApplication
import com.smascaro.trackmixing.common.di.ControllerCompositionRoot
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.NavController
import androidx.navigation.findNavController
import com.smascaro.trackmixing.R

open class BaseActivity : AppCompatActivity() {
    private var mControllerCompositionRoot: ControllerCompositionRoot? = null
    private lateinit var mNavigationController: NavController

    protected fun getCompositionRoot(): ControllerCompositionRoot {
        if (mControllerCompositionRoot == null) {
            return ControllerCompositionRoot(
                (application as TrackMixingApplication).getCompositionRoot(),
                this
            )
        }
        return mControllerCompositionRoot!!
    }

    protected fun getNavigationController(): NavController {
        if (!this::mNavigationController.isInitialized) {
            mNavigationController = findNavController(R.id.nav_host_fragment)
        }
        return mNavigationController
    }
}