package com.smascaro.trackmixing.ui.main

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import com.smascaro.trackmixing.R
import com.smascaro.trackmixing.common.TrackMixingApplication
import com.smascaro.trackmixing.ui.common.BaseActivity
import timber.log.Timber
import javax.inject.Inject

class MainActivity : BaseActivity() {
    @Inject
    lateinit var mController: MainActivityController
    override fun onCreate(savedInstanceState: Bundle?) {
        (application as TrackMixingApplication).appComponent.inject(this)
        super.onCreate(savedInstanceState)

        val viewMvc = getCompositionRoot().getViewMvcFactory().getMainActivityViewMvc(null)

        mController.bindViewMvc(viewMvc)
        mController.handleIntent(intent)

        setContentView(viewMvc.getRootView())
    }
}
