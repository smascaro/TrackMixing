package com.smascaro.trackmixing.ui.main

import android.content.Intent
import com.smascaro.trackmixing.networking.NodeApi
import com.smascaro.trackmixing.networking.requestTrack.RequestTrackResponseSchema
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import timber.log.Timber

class MainActivityController(val mNodeApi: NodeApi) {
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

                mNodeApi.requestTrack(url.toString())
                    .enqueue(object : Callback<RequestTrackResponseSchema> {
                        override fun onFailure(
                            call: Call<RequestTrackResponseSchema>,
                            t: Throwable
                        ) {
                            mViewMvc.displayRequestError(t.message ?: "Unknown throwable message")
                        }

                        override fun onResponse(
                            call: Call<RequestTrackResponseSchema>,
                            response: Response<RequestTrackResponseSchema>
                        ) {
                            mViewMvc.displayRequestError("Request was a success. Code ${response.body()?.status?.message ?: "Unkown"}")
                        }

                    })
            }
        }
    }
}