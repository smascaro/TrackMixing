package com.smascaro.trackmixing.search.business.download.usecase

import com.smascaro.trackmixing.base.coroutine.IoCoroutineScope
import com.smascaro.trackmixing.base.network.node.api.NodeApi
import com.smascaro.trackmixing.base.network.node.model.FetchProgressResponseSchema
import com.smascaro.trackmixing.search.business.download.model.DownloadEvents
import com.smascaro.trackmixing.search.business.download.model.FetchSteps
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.greenrobot.eventbus.EventBus
import timber.log.Timber
import javax.inject.Inject

class FetchProgressUseCase @Inject constructor(
    private val nodeApi: NodeApi,
    private val io: com.smascaro.trackmixing.base.coroutine.IoCoroutineScope
) {
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
        if (shouldKeepFetchingProgress(response)) {
            Timber.d("Sleep for $waitingTimeMillis ms")
            delay(waitingTimeMillis)
            Timber.d("Time to check progress again")
            executeInternal(videoId, waitingTimeMillis)
        } else if (response.body.status_code == "FINISHED") {
            EventBus.getDefault().post(DownloadEvents.FinishedProcessing)
        } else if (response.body.status_code == "ERROR") {
            EventBus.getDefault()
                .post(DownloadEvents.ErrorOccurred("Error occurred during track processing"))
        }
    }

    private fun shouldKeepFetchingProgress(response: FetchProgressResponseSchema) =
        response.body.progress < 100 && response.body.status_code != "ERROR" || response.body.progress == 100 && response.body.status_code != "FINISHED"
}