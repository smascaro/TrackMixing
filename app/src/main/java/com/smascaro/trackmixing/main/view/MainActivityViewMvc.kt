package com.smascaro.trackmixing.main.view

import com.smascaro.trackmixing.common.view.architecture.ObservableViewMvc

interface MainActivityViewMvc :
    ObservableViewMvc<MainActivityViewMvc.Listener> {
    interface Listener {
        fun onToolbarBackButtonPressed()
        fun onSettingsMenuButtonClicked()
        fun onPlayerStateChanged()
        fun onSearchMenuButtonClicked()
    }

    fun showMessage(text: String)
    fun startProcessingRequest(url: String)
    fun updateTitle(title: String, enableBackNavigation: Boolean)
    fun cleanUp()
    fun updateBackgroundColor(newBackgroundColor: Int)
    fun updateBackgroundColorToDefault()
}