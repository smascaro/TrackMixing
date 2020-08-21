package com.smascaro.trackmixing.main.controller

import android.content.Intent
import com.smascaro.trackmixing.common.data.datasource.network.NodeApi
import com.smascaro.trackmixing.common.data.network.RequestTrackResponseSchema
import com.smascaro.trackmixing.main.view.MainActivityViewMvc
import com.smascaro.trackmixing.player.business.downloadtrack.TrackDownloadService
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import timber.log.Timber
import javax.inject.Inject

class MainActivityController @Inject constructor(val mNodeApi: NodeApi) {
    private lateinit var mViewMvc: MainActivityViewMvc
    fun bindViewMvc(viewMvc: MainActivityViewMvc) {
        mViewMvc = viewMvc
    }

    fun handleIntent(intent: Intent) {
        if (intent.action == Intent.ACTION_SEND) {
            val url = if (intent.clipData != null && intent.clipData!!.itemCount > 0) {
                intent.clipData?.getItemAt(0)!!.text
            } else ""
            Timber.d(intent.toString())
            if (url.contains("youtube") || url.contains("youtu.be")) {
                mViewMvc.startProcessingRequest(url.toString())
            }
        }
    }

}