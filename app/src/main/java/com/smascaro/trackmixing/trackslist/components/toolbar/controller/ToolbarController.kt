package com.smascaro.trackmixing.trackslist.components.toolbar.controller

import com.smascaro.trackmixing.common.controller.BaseNavigatorController
import com.smascaro.trackmixing.common.utils.navigation.NavigationHelper
import com.smascaro.trackmixing.trackslist.components.toolbar.view.ToolbarViewMvc
import javax.inject.Inject

class ToolbarController @Inject constructor(p_navigationHelper: NavigationHelper) :
    BaseNavigatorController<ToolbarViewMvc>(p_navigationHelper),
    ToolbarViewMvc.Listener {
    fun onStart() {
        viewMvc.registerListener(this)
    }

    fun onStop() {
        viewMvc.unregisterListener(this)
    }

    override fun onSettingsMenuButtonClicked() {
        navigationHelper.toSettings()
    }

    override fun onSearchMenuButtonClicked() {
        navigationHelper.toSearch()
    }

    override fun dispose() {
        viewMvc.unregisterListener(this)
    }
}