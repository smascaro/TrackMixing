package com.smascaro.trackmixing.player.business.downloadtrack.utils

import android.content.Context
import androidx.core.app.NotificationCompat
import com.smascaro.trackmixing.R
import com.smascaro.trackmixing.common.data.model.NotificationData
import com.smascaro.trackmixing.common.error.WrongArgumentType
import com.smascaro.trackmixing.common.utils.DOWNLOAD_NOTIFICATION_ID
import com.smascaro.trackmixing.common.utils.NOTIFICATION_CHANNEL_ID
import com.smascaro.trackmixing.common.utils.ui.NotificationHelper
import com.smascaro.trackmixing.player.business.downloadtrack.model.DownloadProgressState

class DownloadNotificationHelper(context: Context) : NotificationHelper(context) {
    override fun updateNotification(data: NotificationData) {
        if (data !is DownloadProgressState) {
            throw WrongArgumentType("Argument for download notification must be of type DownloadProgressState")

        }
        val progressState = data
        notificationBuilder =
            NotificationCompat.Builder(context, NOTIFICATION_CHANNEL_ID)
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
        notificationManager.notify(DOWNLOAD_NOTIFICATION_ID, notificationBuilder.build())
    }
}