package com.smascaro.trackmixing.trackslist.components.toolbar.controller

import com.smascaro.trackmixing.base.utils.navigation.BaseNavigatorController
import com.smascaro.trackmixing.base.utils.navigation.NavigationHelper
import com.smascaro.trackmixing.trackslist.components.toolbar.view.ToolbarViewMvc
import com.smascaro.trackmixing.trackslist.view.TracksListFragmentDirections
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
        // navigationHelper.toSettings()
        navigationHelper.navigate(TracksListFragmentDirections.actionDestinationTracksListToDestinationSettings())
        // TODO("Manage navigation")
    }

    override fun onSearchMenuButtonClicked() {
        // navigationHelper.toSearch()
        navigationHelper.navigate(
            TracksListFragmentDirections.actionDestinationTracksListToDestinationSearch()
        )
        // TODO("Manage navigation")
    }

    override fun dispose() {
        viewMvc.unregisterListener(this)
    }
}