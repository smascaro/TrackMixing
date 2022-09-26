package com.smascaro.trackmixing.search.business.download.utils

import android.content.Context
import androidx.core.app.NotificationCompat
import com.smascaro.trackmixing.base.exception.WrongArgumentType
import com.smascaro.trackmixing.base.service.NotificationData
import com.smascaro.trackmixing.base.utils.NotificationHelper
import com.smascaro.trackmixing.R
import com.smascaro.trackmixing.search.business.download.model.DownloadProgressState

class DownloadNotificationHelper(context: Context) : NotificationHelper(context) {
    companion object {
        const val NOTIFICATION_ID = 2001
        const val ACTION_START_DOWNLOAD = "ACTION_START_SERVICE"
        const val EXTRA_START_SERVICE_PARAM_KEY = "EXTRA_START_SERVICE_PARAM_KEY"
    }

    override fun updateNotification(data: NotificationData) {
        if (data !is DownloadProgressState) {
            throw WrongArgumentType("Argument for download notification must be of type DownloadProgressState")
        }
        notificationBuilder.apply {
            setSmallIcon(R.drawable.ic_note)
            setContentTitle(data.trackTitle)
            setOnlyAlertOnce(true)
            setShowWhen(true)
            setOngoing(true)
            setStyle(
                NotificationCompat.BigTextStyle()
                    .bigText("${data.statusMessage} - ${data.progress}%")
            )
            priority = NotificationCompat.PRIORITY_HIGH
        }
        notificationManager.notify(NOTIFICATION_ID, notificationBuilder.build())
    }
}