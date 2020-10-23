package com.smascaro.trackmixing.player.business.downloadtrack.business

import com.smascaro.trackmixing.common.data.datasource.network.NodeApi
import com.smascaro.trackmixing.common.data.network.FetchProgressResponseSchema
import com.smascaro.trackmixing.common.data.network.RequestTrackResponseSchema
import com.smascaro.trackmixing.common.di.coroutines.IoCoroutineScope
import com.smascaro.trackmixing.player.business.downloadtrack.model.DownloadEvents
import com.smascaro.trackmixing.player.business.downloadtrack.model.FetchSteps
import kotlinx.coroutines.*
import org.greenrobot.eventbus.EventBus
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import timber.log.Timber
import javax.inject.Inject

class RequestTrackUseCase @Inject constructor(
    private val nodeApi: NodeApi,
    private val io: IoCoroutineScope
) {
    sealed class Result {
        class Success(val videoId: String) : Result()
        class Failure(val error: Throwable) : Result()
    }

    private var videoId: String? = null
    private var lastProgressState: DownloadEvents.ProgressUpdate? = null

    suspend fun executeSuspended(url: String): Result {
        return try {
            val videoId = nodeApi.requestTrackSuspended(url)
            Result.Success(videoId.body.track_id)
        } catch (e: Exception) {
            Timber.e(e)
            Result.Failure(e)
        }
    }

    fun execute(url: String) {
        nodeApi.requestTrack(url)
            .enqueue(object : Callback<RequestTrackResponseSchema> {
                override fun onFailure(
                    call: Call<RequestTrackResponseSchema>,
                    t: Throwable
                ) {
                    notifyNetworkError("Server error")
                }

                override fun onResponse(
                    call: Call<RequestTrackResponseSchema>,
                    response: Response<RequestTrackResponseSchema>
                ) {
                    val response = response.body()
                    if (response != null) {
                        videoId = response.body.track_id
                        startProgressCheck(1000)
                    }
                }
            })
    }

    private fun startProgressCheck(waitingTimeMillis: Long) {
        if (videoId != null) {
            nodeApi.fetchProgress(videoId!!)
                .enqueue(object : Callback<FetchProgressResponseSchema> {
                    override fun onFailure(
                        call: Call<FetchProgressResponseSchema>,
                        t: Throwable
                    ) {
                        notifyNetworkError("Network error while fetching progress for track $videoId")
                    }

                    override fun onResponse(
                        call: Call<FetchProgressResponseSchema>,
                        response: Response<FetchProgressResponseSchema>
                    ) {
                        CoroutineScope(Dispatchers.IO).launch {
                            val response = response.body()
                            if (response != null) {
                                Timber.d("Progress fetched: ${response.body.progress}%")
                                val title = response.body.title ?: ""
                                val progress = response.body.progress
                                val status = response.body.status_message ?: "Waiting..."
                                val progressUpdateEvent = DownloadEvents.ProgressUpdate(
                                    title,
                                    progress,
                                    status,
                                    FetchSteps.ServerProcessStep()
                                )
                                if (progressUpdateEvent != lastProgressState) {
                                    EventBus.getDefault().post(progressUpdateEvent)
                                    Timber.d("ProgressUpdate event posted")
                                }
                                lastProgressState = progressUpdateEvent
                                if (shouldKeepFetchingProgress(response)) {
                                    Timber.d("Sleep for $waitingTimeMillis ms")
                                    runBlocking { delay(waitingTimeMillis) }
                                    Timber.d("Time to check progress again")
                                    startProgressCheck(waitingTimeMillis)
                                } else if (response.body.status_code == "FINISHED") {
                                    EventBus.getDefault().post(DownloadEvents.FinishedProcessing())
                                } else if (response.body.status_code == "ERROR") {
                                    EventBus.getDefault()
                                        .post(DownloadEvents.ErrorOccurred("Error occurred during track processing"))
                                }
                            }
                        }
                    }
                })
        }
    }

    private fun shouldKeepFetchingProgress(response: FetchProgressResponseSchema) =
        response.body.progress < 100 && response.body.status_code != "ERROR" || response.body.progress == 100 && response.body.status_code != "FINISHED"

    private fun notifyNetworkError(message: String) {
        EventBus.getDefault().post(RequestTrackUseCaseResult.NetworkError(message))
    }

    fun getTrackId(): String {
        return videoId ?: ""
    }
}