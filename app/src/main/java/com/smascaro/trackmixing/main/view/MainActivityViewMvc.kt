package com.smascaro.trackmixing.main.view

import com.smascaro.trackmixing.common.view.architecture.ObservableViewMvc
import com.smascaro.trackmixing.common.view.ui.BaseActivity

interface MainActivityViewMvc :
    ObservableViewMvc<MainActivityViewMvc.Listener> {
    interface Listener {
        fun onToolbarBackButtonPressed()
        fun onSettingsMenuButtonClicked()
        fun onPlayerStateChanged()
        fun onSearchMenuButtonClicked()
    }

    fun bindActivity(activity: BaseActivity)
    fun showMessage(text: String)
    fun startProcessingRequest(url: String)
    fun updateTitle(title: String, enableBackNavigation: Boolean)
    fun cleanUp()
    fun updateBackgroundColor(newBackgroundColor: Int)
    fun updateBackgroundColorToDefault()
    fun prepareSearchContextLayout()
    fun prepareTracksListContextLayout()
}