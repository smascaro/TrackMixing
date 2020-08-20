package com.smascaro.trackmixing.player.business.downloadtrack

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.core.os.bundleOf
import com.smascaro.trackmixing.TrackMixingApplication
import com.smascaro.trackmixing.common.data.model.ForegroundNotification
import com.smascaro.trackmixing.common.utils.DOWNLOAD_NOTIFICATION_ACTION_START_DOWNLOAD
import com.smascaro.trackmixing.common.utils.DOWNLOAD_NOTIFICATION_EXTRA_START_SERVICE_PARAM_KEY
import com.smascaro.trackmixing.playbackservice.BaseService
import com.smascaro.trackmixing.player.business.downloadtrack.controller.TrackDownloadController
import javax.inject.Inject

class TrackDownloadService : BaseService(), TrackDownloadController.ServiceActionsDelegate {
    companion object {
        fun start(context: Context, youtubeUrl: String): Boolean {
            val intent = Intent(context, TrackDownloadService::class.java)
            intent.action = DOWNLOAD_NOTIFICATION_ACTION_START_DOWNLOAD
            val extras = Bundle().apply {
                putString(DOWNLOAD_NOTIFICATION_EXTRA_START_SERVICE_PARAM_KEY, youtubeUrl)
            }
            intent.putExtras(extras)
//            intent.putExtras(bundleOf(DOWNLOAD_NOTIFICATION_EXTRA_START_SERVICE_PARAM_KEY to youtubeUrl))
            val componentName = context.startService(intent)
            return componentName != null
        }
    }

    @Inject
    lateinit var controller: TrackDownloadController

    override fun onCreate() {
        (application as TrackMixingApplication).appComponent.inject(this)
        super.onCreate()
        controller.onCreate()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
//        return super.onStartCommand(intent, flags, startId)
        val action = intent?.action
        if (action != null) {
            when (action) {
                DOWNLOAD_NOTIFICATION_ACTION_START_DOWNLOAD -> startDownload(
                    intent.extras?.getString(
                        DOWNLOAD_NOTIFICATION_EXTRA_START_SERVICE_PARAM_KEY
                    )
                )

            }
        }
        return START_STICKY
    }

    private fun startDownload(videoUrl: String?) {
        if (videoUrl != null) {
            controller.startRequest(videoUrl)
        }
    }

    override fun onStartForeground(foregroundNotification: ForegroundNotification) {
        startForeground(foregroundNotification.id, foregroundNotification.notification)
    }

    override fun onStopForeground(removeNotification: Boolean) {
        stopForeground(removeNotification)
    }

    override fun onStopService() {
        stopService(Intent(this, TrackDownloadService::class.java))
    }
}