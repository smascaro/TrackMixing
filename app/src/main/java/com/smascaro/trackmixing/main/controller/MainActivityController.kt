package com.smascaro.trackmixing.main.controller

import android.content.Intent
import com.smascaro.trackmixing.common.controller.BaseController
import com.smascaro.trackmixing.common.data.datasource.network.NodeApi
import com.smascaro.trackmixing.main.view.MainActivityViewMvc
import timber.log.Timber
import javax.inject.Inject

class MainActivityController @Inject constructor(val mNodeApi: NodeApi) :
    BaseController<MainActivityViewMvc>() {

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

}