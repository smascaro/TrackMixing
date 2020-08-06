package com.smascaro.trackmixing.ui.main

import com.smascaro.trackmixing.ui.common.BaseViewMvc
import com.smascaro.trackmixing.ui.common.ObservableViewMvc

interface MainActivityViewMvc : ObservableViewMvc<MainActivityViewMvc.Listener> {
    interface Listener

    fun displayRequestError(text: String)
}