package com.smascaro.trackmixing.ui.main

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import com.smascaro.trackmixing.R
import com.smascaro.trackmixing.ui.common.BaseActivity
import timber.log.Timber

class MainActivity : BaseActivity() {
    private lateinit var mController: MainActivityController
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val viewMvc = getCompositionRoot().getViewMvcFactory().getMainActivityViewMvc(null)
        mController = getCompositionRoot().getMainActivityController()

        mController.bindViewMvc(viewMvc)
        mController.handleIntent(intent)

        setContentView(viewMvc.getRootView())
    }
}
