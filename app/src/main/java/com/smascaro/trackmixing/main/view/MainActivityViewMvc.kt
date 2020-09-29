package com.smascaro.trackmixing.main.view

import com.smascaro.trackmixing.common.view.architecture.ObservableViewMvc

interface MainActivityViewMvc :
    ObservableViewMvc<MainActivityViewMvc.Listener> {
    interface Listener {
        fun onPlayerStateChanged()
    }

    fun showMessage(text: String)
    fun startProcessingRequest(url: String)
    fun updateBackgroundColor(newBackgroundColor: Int)
    fun updateBackgroundColorToDefault()
}