package com.smascaro.trackmixing.settings.view

import com.smascaro.trackmixing.base.utils.UiUtils
import com.smascaro.trackmixing.base.ui.architecture.view.BaseViewMvc
import javax.inject.Inject

class SettingsActivityViewMvcImpl @Inject constructor(private val uiUtils: UiUtils) :
    BaseViewMvc(),
    SettingsActivityViewMvc