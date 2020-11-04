package com.smascaro.trackmixing.search.business.download

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import com.smascaro.trackmixing.TrackMixingApplication
import com.smascaro.trackmixing.playbackservice.BaseService
import com.smascaro.trackmixing.search.business.download.controller.TrackDownloadController
import com.smascaro.trackmixing.search.business.download.utils.DownloadNotificationHelper
import javax.inject.Inject

class TrackDownloadService : BaseService() {
    companion object {
        fun start(context: Context, youtubeUrl: String): Boolean {
            val intent = Intent(context, TrackDownloadService::class.java)
            intent.action = DownloadNotificationHelper.ACTION_START_DOWNLOAD
            val extras = Bundle().apply {
                putString(DownloadNotificationHelper.EXTRA_START_SERVICE_PARAM_KEY, youtubeUrl)
            }
            intent.putExtras(extras)
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
        initializeServiceCallbacks()
    }

    private fun initializeServiceCallbacks() {
        controller.setStopServiceHandler {
            stopService(Intent(this, TrackDownloadService::class.java))
        }
        controller.setStartForegroundHandler { notification ->
            startForeground(notification.id, notification.notification)
        }
        controller.setStopForegroundHandler { removeNotification ->
            stopForeground(removeNotification)
        }
        controller.setErrorHandler { error ->
            Toast.makeText(this, "Error: ${error.localizedMessage}", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        val action = intent?.action
        if (action != null) {
            when (action) {
                DownloadNotificationHelper.ACTION_START_DOWNLOAD -> startDownload(
                    intent.extras?.getString(
                        DownloadNotificationHelper.EXTRA_START_SERVICE_PARAM_KEY
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
}