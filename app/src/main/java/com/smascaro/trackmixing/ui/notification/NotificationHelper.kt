package com.smascaro.trackmixing.ui.notification

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.os.Build
import android.support.v4.media.session.MediaSessionCompat
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.transition.Transition
import com.smascaro.trackmixing.R
import com.smascaro.trackmixing.common.*
import com.smascaro.trackmixing.service.MixPlayerService
import com.smascaro.trackmixing.service.TrackInstrument
import com.smascaro.trackmixing.tracks.Track

class NotificationHelper(private val mContext: Context) {
    private var mNotificationManager: NotificationManagerCompat
    private lateinit var mNotificationBuilder: NotificationCompat.Builder
    private var mThumbnailBitmap: Bitmap? = null
    private var mMediaSession: MediaSessionCompat? = null

    init {
        mNotificationManager = NotificationManagerCompat.from(mContext)

    }

    fun getNotification(): Notification {
        return mNotificationBuilder.build()
    }

    fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val notificationChannel = NotificationChannel(
                NOTIFICATION_CHANNEL_ID,
                NOTIFICATION_CHANNEL_ID, NotificationManager.IMPORTANCE_LOW
            )
            if (mNotificationManager != null) {
                mNotificationManager.createNotificationChannel(notificationChannel)
            }
        }
    }

    fun createNotification(
        track: Track,
        masterPlaying: Boolean,
        vocalsPlaying: Boolean,
        otherPlaying: Boolean,
        bassPlaying: Boolean,
        drumsPlaying: Boolean
    ) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            if (mThumbnailBitmap == null) {
                val futureIcon = Glide
                    .with(mContext)
                    .asBitmap()
                    .load(track.thumbnailUrl)
                    .diskCacheStrategy(DiskCacheStrategy.AUTOMATIC)
                    .into(object : CustomTarget<Bitmap>() {
                        override fun onLoadCleared(placeholder: Drawable?) {
                            if (mThumbnailBitmap != null) {
                                mThumbnailBitmap!!.recycle()
                            }
                        }

                        override fun onResourceReady(
                            resource: Bitmap,
                            transition: Transition<in Bitmap>?
                        ) {
                            mThumbnailBitmap = resource
                            mNotificationBuilder.setLargeIcon(mThumbnailBitmap)
                            mNotificationManager.notify(
                                NOTIFICATION_ID,
                                mNotificationBuilder.build()
                            )
                        }

                    })
            }

            val actionPlayPause = createIntentMaster(masterPlaying)
            val drawablePlayPause = if (masterPlaying) {
                R.drawable.ic_pause
            } else {
                R.drawable.ic_play
            }
            if (mMediaSession == null) {
                mMediaSession =
                    MediaSessionCompat(mContext, NOTIFICATION_MEDIA_SESSION_TAG)
            }
            mNotificationBuilder =
                NotificationCompat.Builder(mContext, NOTIFICATION_CHANNEL_ID).apply {
                    setSmallIcon(R.drawable.ic_note)
                    setContentTitle(track.title)
                    setOnlyAlertOnce(true)
                    setShowWhen(false)
                    addAction(drawablePlayPause, "Play/Pause", actionPlayPause)
                    setStyle(
                        androidx.media.app.NotificationCompat.MediaStyle()
                            .setShowActionsInCompactView(0)
                            .setMediaSession(mMediaSession?.sessionToken)
                    )
                    priority = NotificationCompat.PRIORITY_HIGH
                }
            if (mThumbnailBitmap != null) {
                mNotificationBuilder.setLargeIcon(mThumbnailBitmap)
            }
            mNotificationManager.notify(NOTIFICATION_ID, mNotificationBuilder.build())
        } else {
            //Do nothing
        }
    }


    @RequiresApi(Build.VERSION_CODES.O)
    private fun createIntentTrack(instrument: TrackInstrument, isPlaying: Boolean): PendingIntent {
        val intent = Intent(mContext, MixPlayerService::class.java)
        val action = when (instrument) {
            TrackInstrument.VOCALS -> when (isPlaying) {
                true -> NOTIFICATION_ACTION_MUTE_VOCALS
                false -> NOTIFICATION_ACTION_UNMUTE_VOCALS
            }
            else -> ""
        }
        intent.action = action
        val pendingIntent = PendingIntent.getForegroundService(
            mContext,
            1,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT
        )
        return pendingIntent
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun createIntentMaster(isPlaying: Boolean): PendingIntent {
        val intent = Intent(mContext, MixPlayerService::class.java)
        val action = when (isPlaying) {
            true -> NOTIFICATION_ACTION_PAUSE_MASTER
            false -> NOTIFICATION_ACTION_PLAY_MASTER
        }

        intent.action = action
        val pendingIntent = PendingIntent.getForegroundService(
            mContext,
            2,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT
        )
        return pendingIntent
    }


}