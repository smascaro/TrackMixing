package com.smascaro.trackmixing.ui.main

import android.os.Bundle
import android.view.LayoutInflater
import com.smascaro.trackmixing.R
import com.smascaro.trackmixing.common.TrackMixingApplication
import com.smascaro.trackmixing.ui.common.BaseActivity
import javax.inject.Inject

class MainActivity : BaseActivity() {
    @Inject
    lateinit var mainActivityController: MainActivityController

    @Inject
    lateinit var viewMvc: MainActivityViewMvc

    override fun onCreate(savedInstanceState: Bundle?) {
        (application as TrackMixingApplication).appComponent.inject(this)
        super.onCreate(savedInstanceState)

        val rootView = LayoutInflater.from(this).inflate(R.layout.activity_main, null, false)
        viewMvc.bindRootView(rootView)
        mainActivityController.bindViewMvc(viewMvc)
        mainActivityController.handleIntent(intent)

        setContentView(rootView)
    }
}
