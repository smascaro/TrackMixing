package com.smascaro.trackmixing.trackslist.components.toolbar.controller

import com.smascaro.trackmixing.base.utils.navigation.BaseNavigatorController
import com.smascaro.trackmixing.base.utils.navigation.NavigationHelper
import com.smascaro.trackmixing.trackslist.components.toolbar.view.ToolbarViewMvc
import com.smascaro.trackmixing.trackslist.view.TracksListFragmentDirections
import javax.inject.Inject

class ToolbarController @Inject constructor(navigationHelper: NavigationHelper) :
    BaseNavigatorController<ToolbarViewMvc>(navigationHelper),
    ToolbarViewMvc.Listener {
    fun onStart() {
        viewMvc.registerListener(this)
    }

    fun onStop() {
        viewMvc.unregisterListener(this)
    }

    override fun onSettingsMenuButtonClicked() {
        navigationHelper.navigate(
            TracksListFragmentDirections.actionDestinationTracksListToDestinationSettings()
        )
    }

    override fun onSearchMenuButtonClicked() {
        navigationHelper.navigate(
            TracksListFragmentDirections.actionDestinationTracksListToDestinationSearch()
        )
    }

    override fun dispose() {
        viewMvc.unregisterListener(this)
    }
}