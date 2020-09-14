package com.smascaro.trackmixing.settings.view

import android.view.View
import com.smascaro.trackmixing.common.utils.ui.UiUtils
import com.smascaro.trackmixing.common.view.architecture.BaseViewMvc
import javax.inject.Inject

class SettingsActivityViewMvcImpl @Inject constructor(private val uiUtils: UiUtils) :
    BaseViewMvc(),
    SettingsActivityViewMvc {

    override fun bindRootView(rootView: View?) {
        super.bindRootView(rootView)
        initialize()
    }

    private fun initialize() {
    }
}