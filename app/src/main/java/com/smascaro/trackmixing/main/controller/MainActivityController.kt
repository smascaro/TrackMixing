package com.smascaro.trackmixing.main.controller

import android.content.Intent
import com.smascaro.trackmixing.common.controller.BaseNavigatorController
import com.smascaro.trackmixing.common.utils.NavigationHelper
import com.smascaro.trackmixing.main.view.MainActivityViewMvc
import timber.log.Timber
import javax.inject.Inject

class MainActivityController @Inject constructor(p_navigationHelper: NavigationHelper) :
    BaseNavigatorController<MainActivityViewMvc>(p_navigationHelper), MainActivityViewMvc.Listener {

    fun onStart() {
        viewMvc.registerListener(this)
    }

    fun onStop() {
        viewMvc.unregisterListener(this)
    }

    fun handleIntent(intent: Intent) {
        if (intent.action == Intent.ACTION_SEND) {
            val url = if (intent.clipData != null && intent.clipData!!.itemCount > 0) {
                intent.clipData?.getItemAt(0)!!.text
            } else ""
            Timber.d(intent.toString())
            if (url.contains("youtube") || url.contains("youtu.be")) {
                viewMvc.startProcessingRequest(url.toString())
            }
        }
    }

    fun updateTitle(title: String, enableBackNavigation: Boolean) {
        viewMvc.updateTitle(title, enableBackNavigation)
    }

    override fun onToolbarBackButtonPressed() {
        viewMvc.cleanUp()
        navigationHelper.back()
    }
}