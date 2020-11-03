package com.smascaro.trackmixing.common.utils.ui

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.smascaro.trackmixing.common.data.model.NotificationData

abstract class NotificationHelper(protected val context: Context) {
    companion object {
        const val CHANNEL_ID = "NOTIFICATION_CHANNEL_ID"
    }

    protected val notificationManager: NotificationManagerCompat = NotificationManagerCompat.from(context)
    protected var notificationBuilder = NotificationCompat.Builder(context, CHANNEL_ID)

    init {
        createNotificationChannel()
    }

    fun createBuilder() = NotificationCompat.Builder(context, CHANNEL_ID)
    fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val notificationChannel = NotificationChannel(
                CHANNEL_ID,
                "TrackMixing notification channel", NotificationManager.IMPORTANCE_LOW
            )
            notificationManager.createNotificationChannel(notificationChannel)
        }
    }

    fun getNotification(): Notification {
        return notificationBuilder.build()
    }

    abstract fun updateNotification(data: NotificationData)
}