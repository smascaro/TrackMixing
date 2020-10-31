package com.smascaro.trackmixing.player.business.downloadtrack.utils

import android.content.Context
import androidx.core.app.NotificationCompat
import com.smascaro.trackmixing.R
import com.smascaro.trackmixing.common.data.model.NotificationData
import com.smascaro.trackmixing.common.error.WrongArgumentType
import com.smascaro.trackmixing.common.utils.ui.NotificationHelper
import com.smascaro.trackmixing.player.business.downloadtrack.model.DownloadProgressState

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
        val progressState = data
        notificationBuilder =
            NotificationCompat.Builder(context, CHANNEL_ID)
                .apply {
                    setSmallIcon(R.drawable.ic_note)
                    setContentTitle(progressState.trackTitle)
                    setOnlyAlertOnce(true)
                    setShowWhen(true)
                    setOngoing(true)
                    setStyle(
                        NotificationCompat.BigTextStyle()
                            .bigText("${progressState.statusMessage} - ${progressState.progress}%")
                    )
                    priority = NotificationCompat.PRIORITY_HIGH
                }
        notificationManager.notify(NOTIFICATION_ID, notificationBuilder.build())
    }
}