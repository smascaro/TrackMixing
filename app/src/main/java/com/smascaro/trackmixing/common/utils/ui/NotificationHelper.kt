package com.smascaro.trackmixing.common.utils.ui

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.smascaro.trackmixing.common.data.model.NotificationData
import com.smascaro.trackmixing.common.utils.NOTIFICATION_CHANNEL_ID

abstract class NotificationHelper(protected val context: Context) {
    protected val notificationManager: NotificationManagerCompat
    protected lateinit var notificationBuilder: NotificationCompat.Builder

    init {
        notificationManager = NotificationManagerCompat.from(context)
        createNotificationChannel()
    }

    fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val notificationChannel = NotificationChannel(
                NOTIFICATION_CHANNEL_ID,
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