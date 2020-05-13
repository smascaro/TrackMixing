package com.smascaro.trackmixing.tracks

import android.util.Log
import com.smascaro.trackmixing.data.toModel
import com.smascaro.trackmixing.networking.NodeApi
import com.smascaro.trackmixing.networking.availableTracks.AvailableTracksResponseSchema
import com.smascaro.trackmixing.ui.common.BaseObservable
import com.smascaro.trackmixing.ui.common.BaseObservableViewMvc
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import timber.log.Timber
import java.util.ArrayList

class FetchAvailableTracksUseCase(private val mNodeApi: NodeApi) :
    BaseObservable<FetchAvailableTracksUseCase.Listener>() {

    interface Listener {
        fun onAvailableTracksFetched(tracks: List<Track>)
        fun onAvailableTracksFetchFailed()
    }


    fun fetchAvailableTracksAndNotify() {
        mNodeApi.fetchAvailableTracks("newest", null, null)
            .enqueue(object : Callback<AvailableTracksResponseSchema> {
                override fun onResponse(
                    call: Call<AvailableTracksResponseSchema>,
                    response: Response<AvailableTracksResponseSchema>
                ) {
                    if (response.isSuccessful) {
                        val responseBody = response.body()
                        if (responseBody != null) {
                            if (responseBody.result.code == 0) {
                                notifySuccess(responseBody)
                            } else {
                                notifyFailure(responseBody.result.message)
                            }
                        } else {
                            Timber.e("Response body was empty/null")
                        }
                    }
                }

                override fun onFailure(call: Call<AvailableTracksResponseSchema>, t: Throwable) {

                    notifyFailure(t.message ?: "Unknown error")
                }

            })
    }

    private fun notifyFailure(msg: String) {
        Timber.e(msg)
    }

    private fun notifySuccess(tracksResponseSchema: AvailableTracksResponseSchema) {
        var tracks = tracksResponseSchema.items.map {
            it.toModel()
        }
        getListeners().forEach { listener ->
            listener.onAvailableTracksFetched(tracks)
        }
    }
}