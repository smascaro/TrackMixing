package com.smascaro.trackmixing.trackslist.components.toolbar.view

import com.smascaro.trackmixing.common.view.architecture.ObservableViewMvc

interface ToolbarViewMvc : ObservableViewMvc<ToolbarViewMvc.Listener> {
    interface Listener {
        fun onSettingsMenuButtonClicked()
        fun onSearchMenuButtonClicked()
    }
}