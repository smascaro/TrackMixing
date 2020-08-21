package com.smascaro.trackmixing.main.controller

import com.smascaro.trackmixing.common.controller.BaseController
import com.smascaro.trackmixing.common.error.ViewMvcNotInitialized
import com.smascaro.trackmixing.common.view.architecture.ViewMvc
import com.smascaro.trackmixing.main.view.BottomProgressViewMvc
import javax.inject.Inject

class BottomProgressController @Inject constructor() : BaseController<BottomProgressViewMvc>() {

    fun onCreate() {
        viewMvc.startMarquee()
    }


}

