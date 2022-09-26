package com.smascaro.trackmixing.trackslist.components.toolbar.view

import com.smascaro.trackmixing.base.ui.architecture.view.ObservableViewMvc

interface ToolbarViewMvc : ObservableViewMvc<ToolbarViewMvc.Listener> {
    interface Listener {
        fun onSettingsMenuButtonClicked()
        fun onSearchMenuButtonClicked()
    }
}