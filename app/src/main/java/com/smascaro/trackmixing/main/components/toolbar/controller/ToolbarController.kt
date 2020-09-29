package com.smascaro.trackmixing.main.components.toolbar.controller

import com.smascaro.trackmixing.common.controller.BaseNavigatorController
import com.smascaro.trackmixing.common.utils.navigation.NavigationDestination
import com.smascaro.trackmixing.common.utils.navigation.NavigationHelper
import com.smascaro.trackmixing.main.components.toolbar.view.ToolbarViewMvc
import javax.inject.Inject

class ToolbarController @Inject constructor(p_navigationHelper: NavigationHelper) :
    BaseNavigatorController<ToolbarViewMvc>(p_navigationHelper),
    ToolbarViewMvc.Listener,
    NavigationHelper.Listener {

    fun onStart() {
        viewMvc.registerListener(this)
        navigationHelper.registerListener(this)
    }

    fun onStop() {
        viewMvc.unregisterListener(this)
        navigationHelper.unregisterListener(this)
    }

    fun updateTitle(title: String, enableBackNavigation: Boolean) {
        viewMvc.updateTitle(title, enableBackNavigation)
    }

    override fun onToolbarBackButtonPressed() {
        viewMvc.cleanUp()
        navigationHelper.back()
    }

    override fun onSettingsMenuButtonClicked() {
        navigationHelper.toSettings()
    }

    override fun onSearchMenuButtonClicked() {
        navigationHelper.toSearch()
    }

    override fun onDestinationChange(destination: NavigationDestination) {
        when (destination) {
            is NavigationDestination.Search -> handleNavigationToSearch()
            is NavigationDestination.TracksList -> handleNavigationToTracksList()
        }
    }

    private fun handleNavigationToTracksList() {
        viewMvc.prepareTracksListContextLayout()
    }

    private fun handleNavigationToSearch() {
        viewMvc.prepareSearchContextLayout()
    }
}