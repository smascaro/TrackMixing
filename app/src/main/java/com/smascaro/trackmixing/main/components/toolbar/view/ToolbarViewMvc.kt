package com.smascaro.trackmixing.main.components.toolbar.view

import com.smascaro.trackmixing.common.view.architecture.ObservableViewMvc
import com.smascaro.trackmixing.common.view.ui.BaseActivity

interface ToolbarViewMvc : ObservableViewMvc<ToolbarViewMvc.Listener> {
    interface Listener {
        fun onToolbarBackButtonPressed()
        fun onSettingsMenuButtonClicked()
        fun onSearchMenuButtonClicked()
    }

    fun bindActivity(activity: BaseActivity)
    fun updateTitle(title: String, enableBackNavigation: Boolean)
    fun prepareSearchContextLayout()
    fun prepareTracksListContextLayout()
    fun cleanUp()
}