package com.smascaro.trackmixing.base.ui.architecture.controller

import com.smascaro.trackmixing.base.exception.ViewMvcNotInitialized
import com.smascaro.trackmixing.base.ui.architecture.view.ViewMvc

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

    /**
     * Method to be called when instance is not needed anymore. Release resources here.
     */
    open fun dispose() {
        // To be overridden by implementors.
    }
}