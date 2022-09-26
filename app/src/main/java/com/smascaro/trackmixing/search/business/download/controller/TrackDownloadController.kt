package com.smascaro.trackmixing.search.business.download.controller

import com.smascaro.trackmixing.base.coroutine.IoCoroutineScope
import com.smascaro.trackmixing.base.coroutine.MainCoroutineScope
import com.smascaro.trackmixing.base.di.module.notification.DownloadNotificationHelperImplementation
import com.smascaro.trackmixing.base.events.ApplicationEvent
import com.smascaro.trackmixing.base.events.ApplicationEvent.AppState
import com.smascaro.trackmixing.base.events.UiProgressEvent
import com.smascaro.trackmixing.base.service.ForegroundNotification
import com.smascaro.trackmixing.base.service.ServiceCallbackHandler
import com.smascaro.trackmixing.base.utils.NotificationHelper
import com.smascaro.trackmixing.search.business.download.model.DownloadEvents
import com.smascaro.trackmixing.search.business.download.model.FetchSteps
import com.smascaro.trackmixing.search.business.download.model.evaluateOverallProgress
import com.smascaro.trackmixing.search.business.download.model.toNotificationData
import com.smascaro.trackmixing.search.business.download.usecase.DownloadTrackUseCase
import com.smascaro.trackmixing.search.business.download.usecase.FetchProgressUseCase
import com.smascaro.trackmixing.search.business.download.usecase.RequestTrackUseCase
import com.smascaro.trackmixing.search.business.download.usecase.RequestTrackUseCaseResult
import com.smascaro.trackmixing.search.business.download.utils.DownloadNotificationHelper
import kotlinx.coroutines.launch
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import timber.log.Timber
import javax.inject.Inject

class TrackDownloadController @Inject constructor(
    @DownloadNotificationHelperImplementation private val notificationHelper: NotificationHelper,
    private val requestTrackUseCase: RequestTrackUseCase,
    private val fetchProgressUseCase: FetchProgressUseCase,
    private val downloadTrackUseCase: DownloadTrackUseCase,
    private val eventBus: EventBus,
    private val io: com.smascaro.trackmixing.base.coroutine.IoCoroutineScope,
    private val ui: com.smascaro.trackmixing.base.coroutine.MainCoroutineScope
) : ServiceCallbackHandler() {

    private var applicationState: AppState = AppState.Foreground
    private var lastProgressEvent: DownloadEvents.ProgressUpdate =
        DownloadEvents.ProgressUpdate("Waiting for data...", 0, "", FetchSteps.ServerProcessStep())
    private var currentRequestedTrackId: String? = null

    fun onCreate() {
        eventBus.register(this)
    }

    fun startRequest(videoUrl: String) {
        ui.launch {
            val resultRequestUseCase = requestTrackUseCase.execute(videoUrl)
            when (resultRequestUseCase) {
                is RequestTrackUseCase.Result.Success -> handleRequestSuccess(resultRequestUseCase.videoId)
                is RequestTrackUseCase.Result.Failure -> handleRequestError(resultRequestUseCase.error)
            }
        }
    }

    private fun handleRequestError(error: Throwable) {
        Timber.e(error)
        handleError(error)
    }

    private fun handleRequestSuccess(videoId: String) {
        currentRequestedTrackId = videoId
        fetchProgressUseCase.execute(currentRequestedTrackId!!, 1000)
    }

    private fun goBackground() {
        applicationState = AppState.Background
        notificationHelper.updateNotification(lastProgressEvent.toNotificationData())
        notifyStartForeground()
    }

    private fun goForeground() {
        applicationState = AppState.Foreground
        notifyStopForeground(true)
        eventBus.post(lastProgressEvent)
    }

    @Subscribe(threadMode = ThreadMode.BACKGROUND)
    fun onMessageEvent(applicationEvent: ApplicationEvent) {
        Timber.d("Application is now in ${applicationEvent.state}")
        when (applicationEvent.state) {
            is AppState.Background -> goBackground()
            is AppState.Foreground -> goForeground()
        }
    }

    @Subscribe(threadMode = ThreadMode.BACKGROUND)
    fun onMessageEvent(progressUpdate: DownloadEvents.ProgressUpdate) {
        Timber.d("Received event of type ProgressUpdate -> $progressUpdate")
        when (applicationState) {
            is AppState.Background -> handleProgressBackground(progressUpdate)
            is AppState.Foreground -> handleProgressForeground(progressUpdate)
        }
        lastProgressEvent = progressUpdate
    }

    private fun handleProgressBackground(progressUpdate: DownloadEvents.ProgressUpdate) =
        notificationHelper.updateNotification(progressUpdate.toNotificationData())

    private fun handleProgressForeground(progressUpdate: DownloadEvents.ProgressUpdate) {
        val progress = progressUpdate.evaluateOverallProgress()
        Timber.d("Overall progress: $progress")
        eventBus.post(UiProgressEvent.ProgressUpdate(progress, progressUpdate.message))
    }

    @Subscribe(threadMode = ThreadMode.BACKGROUND)
    fun onMessageEvent(finishedProcessing: DownloadEvents.FinishedProcessing) {
        Timber.d("Received event of type FinishedProcessing")
        io.launch {
            downloadGeneratedTracks()
        }
    }

    private suspend fun downloadGeneratedTracks() =
        downloadTrackUseCase.execute(currentRequestedTrackId!!)

    @Subscribe(threadMode = ThreadMode.BACKGROUND)
    fun onMessageEvent(finishedDownloading: DownloadEvents.FinishedDownloading) {
        Timber.d("Received event of type FinishedDownloading")

        if (applicationState is AppState.Foreground) {
            eventBus.post(UiProgressEvent.ProgressFinished)
        }
        eventBus.unregister(this)
        notifyStopForeground(true)
        notifyStopService()
    }

    @Subscribe(threadMode = ThreadMode.BACKGROUND)
    fun onMessageEvent(errorOccurred: DownloadEvents.ErrorOccurred) {
        Timber.d("Received event of type ErrorOccurred")
        Timber.w(errorOccurred.message)
        eventBus.post(UiProgressEvent.ErrorOccurred(errorOccurred.message))
        eventBus.unregister(this)
        notifyStopService()
    }

    @Subscribe(threadMode = ThreadMode.BACKGROUND)
    fun onMessageEvent(networkError: RequestTrackUseCaseResult.NetworkError) {
        Timber.d("Received event of type ErrorOccurred")
        Timber.e(networkError.message)
        eventBus.post(UiProgressEvent.ErrorOccurred(networkError.message))
        eventBus.unregister(this)
        notifyStopService()
    }

    private fun notifyStopForeground(removeNotification: Boolean) =
        handleStopForeground(removeNotification)

    private fun notifyStopService() =
        handleStopService()

    private fun notifyStartForeground() {
        handleStartForeground(
            ForegroundNotification(
                DownloadNotificationHelper.NOTIFICATION_ID,
                notificationHelper.getNotification()
            )
        )
    }
}
