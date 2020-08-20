package com.smascaro.trackmixing.player.business.downloadtrack.controller

import com.smascaro.trackmixing.common.data.datasource.network.NodeApi
import com.smascaro.trackmixing.common.data.model.ForegroundNotification
import com.smascaro.trackmixing.common.di.DownloadNotificationHelperImplementation
import com.smascaro.trackmixing.common.view.architecture.BaseObservable
import com.smascaro.trackmixing.common.utils.NotificationHelper
import com.smascaro.trackmixing.player.business.DownloadTrackUseCase
import com.smascaro.trackmixing.player.business.downloadtrack.business.RequestTrackUseCase
import com.smascaro.trackmixing.player.business.downloadtrack.model.ApplicationEvent
import com.smascaro.trackmixing.player.business.downloadtrack.model.ApplicationEvent.AppState
import com.smascaro.trackmixing.player.business.downloadtrack.model.DownloadEvents
import com.smascaro.trackmixing.player.business.downloadtrack.model.DownloadProgressState
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import timber.log.Timber
import javax.inject.Inject

class TrackDownloadController @Inject constructor(
    val nodeApi: NodeApi,
    @DownloadNotificationHelperImplementation val notificationHelper: NotificationHelper,
    private val requestTrackUseCase: RequestTrackUseCase,
    private val downloadTrackUseCase: DownloadTrackUseCase
) :
    BaseObservable<TrackDownloadController.ServiceActionsDelegate>() {
    interface ServiceActionsDelegate {
        fun onStartForeground(foregroundNotification: ForegroundNotification)
        fun onStopForeground(removeNotification: Boolean)
        fun onStopService()
    }

    private var applicationState: AppState = AppState.Foreground()

    fun onCreate() {
        EventBus.getDefault().register(this)
    }

    private fun goBackground() {
        applicationState = AppState.Background()
//        TODO("Not yet implemented")
    }

    private fun goForeground() {
        applicationState = AppState.Foreground()
//        TODO("Not yet implemented")
    }

    fun startRequest(videoUrl: String) {
        requestTrackUseCase.execute(videoUrl)
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
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
    }


    private fun handleProgressBackground(progressUpdate: DownloadEvents.ProgressUpdate) {
        notificationHelper.updateNotification(
            DownloadProgressState(
                progressUpdate.trackTitle,
                progressUpdate.progress,
                progressUpdate.message
            )
        )
    }

    private fun handleProgressForeground(progressUpdate: DownloadEvents.ProgressUpdate) {
//        TODO("Not yet implemented")
    }

    @Subscribe(threadMode = ThreadMode.BACKGROUND)
    fun onMessageEvent(finishedProcessing: DownloadEvents.FinishedProcessing) {
        Timber.d("Received event of type FinishedProcessing")
        EventBus.getDefault().unregister(this)
        getListeners().forEach {
            it.onStopService()
        }
    }

    @Subscribe(threadMode = ThreadMode.BACKGROUND)
    fun onMessageEvent(errorOccurred: DownloadEvents.ErrorOccurred) {
        Timber.d("Received event of type ErrorOccurred")
        Timber.w(errorOccurred.message)
        EventBus.getDefault().unregister(this)
        getListeners().forEach {
            it.onStopService()
        }
    }

}