package com.smascaro.trackmixing.main.view

import com.smascaro.trackmixing.base.ui.architecture.view.ObservableViewMvc

interface MainActivityViewMvc :
    ObservableViewMvc<MainActivityViewMvc.Listener> {
    interface Listener {
        fun onPlayerStateChanged()
    }

    fun showMessage(text: String)
    fun updateBackgroundColor(newBackgroundColor: Int)
    fun updateBackgroundColorToDefault()
}