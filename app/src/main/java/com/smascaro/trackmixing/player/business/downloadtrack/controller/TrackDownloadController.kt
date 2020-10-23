package com.smascaro.trackmixing.player.business.downloadtrack.controller

import com.smascaro.trackmixing.common.data.datasource.network.NodeApi
import com.smascaro.trackmixing.common.data.model.ForegroundNotification
import com.smascaro.trackmixing.common.di.DownloadNotificationHelperImplementation
import com.smascaro.trackmixing.common.utils.DOWNLOAD_NOTIFICATION_ID
import com.smascaro.trackmixing.common.utils.ui.NotificationHelper
import com.smascaro.trackmixing.common.view.architecture.BaseObservable
import com.smascaro.trackmixing.main.components.progress.model.UiProgressEvent
import com.smascaro.trackmixing.player.business.DownloadTrackUseCase
import com.smascaro.trackmixing.player.business.downloadtrack.business.FetchProgressUseCase
import com.smascaro.trackmixing.player.business.downloadtrack.business.RequestTrackUseCase
import com.smascaro.trackmixing.player.business.downloadtrack.business.RequestTrackUseCaseResult
import com.smascaro.trackmixing.player.business.downloadtrack.model.*
import com.smascaro.trackmixing.player.business.downloadtrack.model.ApplicationEvent.AppState
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import timber.log.Timber
import javax.inject.Inject

class TrackDownloadController @Inject constructor(
    val nodeApi: NodeApi,
    @DownloadNotificationHelperImplementation val notificationHelper: NotificationHelper,
    private val requestTrackUseCase: RequestTrackUseCase,
    private val fetchProgressUseCase: FetchProgressUseCase,
    private val downloadTrackUseCase: DownloadTrackUseCase
) :
    BaseObservable<TrackDownloadController.ServiceActionsDelegate>() {
    interface ServiceActionsDelegate {
        fun onStartForeground(foregroundNotification: ForegroundNotification)
        fun onStopForeground(removeNotification: Boolean)
        fun onStopService()
    }

    private var applicationState: AppState = AppState.Foreground()
    private var lastProgressEvent: DownloadEvents.ProgressUpdate =
        DownloadEvents.ProgressUpdate("Waiting for data...", 0, "", FetchSteps.ServerProcessStep())

    fun onCreate() {
        EventBus.getDefault().register(this)
    }

    fun startRequest(videoUrl: String) {
        CoroutineScope(Dispatchers.Main).launch {
            val resultRequestUseCase = requestTrackUseCase.executeSuspended(videoUrl)
            when (resultRequestUseCase) {
                is RequestTrackUseCase.Result.Success -> handleRequestSuccess(resultRequestUseCase.videoId)
                is RequestTrackUseCase.Result.Failure -> handleRequestError(resultRequestUseCase.error)
            }
        }
    }

    private fun handleRequestError(error: Throwable) {
        Timber.e(error)
    }

    private fun handleRequestSuccess(videoId: String) {
        fetchProgressUseCase.execute(videoId, 1000)
    }

    private fun goBackground() {
        applicationState = AppState.Background()
        notificationHelper.updateNotification(lastProgressEvent.toNotificationData())
        notifyStartForeground()
    }

    private fun goForeground() {
        applicationState = AppState.Foreground()
        notifyStopForeground(true)
        EventBus.getDefault().post(lastProgressEvent)
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

    private fun handleProgressBackground(progressUpdate: DownloadEvents.ProgressUpdate) {
        notificationHelper.updateNotification(progressUpdate.toNotificationData())
    }

    private fun handleProgressForeground(progressUpdate: DownloadEvents.ProgressUpdate) {
        val progress = progressUpdate.evaluateOverallProgress()
        Timber.d("Overall progress: $progress")
        EventBus.getDefault()
            .post(UiProgressEvent.ProgressUpdate(progress, progressUpdate.message))
    }

    private fun downloadAndUnzipTrack() {
        downloadTrackUseCase.execute(requestTrackUseCase.getTrackId())
    }

    @Subscribe(threadMode = ThreadMode.BACKGROUND)
    fun onMessageEvent(finishedProcessing: DownloadEvents.FinishedProcessing) {
        Timber.d("Received event of type FinishedProcessing")
        downloadAndUnzipTrack()
    }

    @Subscribe(threadMode = ThreadMode.BACKGROUND)
    fun onMessageEvent(finishedDownloading: DownloadEvents.FinishedDownloading) {
        Timber.d("Received event of type FinishedDownloading")

        if (applicationState is AppState.Foreground) {
            EventBus.getDefault().post(UiProgressEvent.ProgressFinished())
        }
        EventBus.getDefault().unregister(this)
        notifyStopForeground(true)
        notifyStopService()
    }

    @Subscribe(threadMode = ThreadMode.BACKGROUND)
    fun onMessageEvent(errorOccurred: DownloadEvents.ErrorOccurred) {
        Timber.d("Received event of type ErrorOccurred")
        Timber.w(errorOccurred.message)
        EventBus.getDefault().post(UiProgressEvent.ErrorOccurred(errorOccurred.message))
        EventBus.getDefault().unregister(this)
        notifyStopService()
    }

    @Subscribe(threadMode = ThreadMode.BACKGROUND)
    fun onMessageEvent(networkError: RequestTrackUseCaseResult.NetworkError) {
        Timber.d("Received event of type ErrorOccurred")
        Timber.e(networkError.message)
        EventBus.getDefault().post(UiProgressEvent.ErrorOccurred(networkError.message))
        EventBus.getDefault().unregister(this)
        notifyStopService()
    }

    private fun notifyStopForeground(removeNotification: Boolean) {
        getListeners().forEach {
            it.onStopForeground(removeNotification)
        }
    }

    private fun notifyStopService() {
        getListeners().forEach {
            it.onStopService()
        }
    }

    private fun notifyStartForeground() {
        getListeners().forEach {
            it.onStartForeground(
                ForegroundNotification(
                    DOWNLOAD_NOTIFICATION_ID,
                    notificationHelper.getNotification()
                )
            )
        }
    }
}
