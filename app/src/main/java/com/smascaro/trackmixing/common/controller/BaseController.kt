package com.smascaro.trackmixing.common.controller

import com.smascaro.trackmixing.common.error.ViewMvcNotInitialized
import com.smascaro.trackmixing.common.view.architecture.ViewMvc

abstract class BaseController<VIEW_MVC : ViewMvc> {
    protected lateinit var viewMvc: VIEW_MVC
    fun bindViewMvc(viewMvc: VIEW_MVC) {
        this.viewMvc = viewMvc
    }

    protected fun ensureViewMvcBound() {
        if (!this::viewMvc.isInitialized) {
            throw ViewMvcNotInitialized("ViewMvc must be initialized as soon as possible")
        }
    }
}