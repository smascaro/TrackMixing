package com.smascaro.trackmixing.player.business.downloadtrack.business

import com.smascaro.trackmixing.common.data.datasource.network.NodeApi
import com.smascaro.trackmixing.common.data.network.FetchProgressResponseSchema
import com.smascaro.trackmixing.common.di.coroutines.IoCoroutineScope
import com.smascaro.trackmixing.player.business.downloadtrack.model.DownloadEvents
import com.smascaro.trackmixing.player.business.downloadtrack.model.FetchSteps
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.greenrobot.eventbus.EventBus
import timber.log.Timber
import javax.inject.Inject

class FetchProgressUseCase @Inject constructor(
    private val nodeApi: NodeApi,
    private val io: IoCoroutineScope
) {
    sealed class Result {
        class Success(val videoId: String) : Result()
        class Failure(val error: Throwable) : Result()
    }

    fun execute(videoId: String, waitingTimeMillis: Long) {
        io.launch { executeInternal(videoId, waitingTimeMillis) }
    }

    private suspend fun executeInternal(videoId: String, waitingTimeMillis: Long) {
        var lastProgressState: DownloadEvents.ProgressUpdate? = null
        val response = nodeApi.fetchProgress(videoId)
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
            delay(waitingTimeMillis)
            Timber.d("Time to check progress again")
            executeInternal(videoId, waitingTimeMillis)
        } else if (response.body.status_code == "FINISHED") {
            EventBus.getDefault().post(DownloadEvents.FinishedProcessing())
        } else if (response.body.status_code == "ERROR") {
            EventBus.getDefault()
                .post(DownloadEvents.ErrorOccurred("Error occurred during track processing"))
        }
    }

    private fun shouldKeepFetchingProgress(response: FetchProgressResponseSchema) =
        response.body.progress < 100 && response.body.status_code != "ERROR" || response.body.progress == 100 && response.body.status_code != "FINISHED"
}