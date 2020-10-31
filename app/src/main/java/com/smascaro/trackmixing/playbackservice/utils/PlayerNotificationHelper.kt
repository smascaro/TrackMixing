package com.smascaro.trackmixing.playbackservice.utils

import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.support.v4.media.session.MediaSessionCompat
import androidx.core.app.NotificationCompat
import com.bumptech.glide.RequestManager
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.transition.Transition
import com.smascaro.trackmixing.R
import com.smascaro.trackmixing.common.data.model.NotificationData
import com.smascaro.trackmixing.common.error.WrongArgumentType
import com.smascaro.trackmixing.common.utils.ui.NotificationHelper
import com.smascaro.trackmixing.main.view.MainActivity
import com.smascaro.trackmixing.playbackservice.MixPlayerService
import com.smascaro.trackmixing.playbackservice.model.MixPlaybackState
import javax.inject.Inject

class PlayerNotificationHelper @Inject constructor(
    context: Context,
    val glide: RequestManager
) : NotificationHelper(context) {
    companion object {
        const val NOTIFICATION_ID = 2000
        const val MEDIA_SESSION_TAG = "MEDIA_SESSION_TAG"
    }

    private var mThumbnailBitmap: Bitmap? = null
    private var mMediaSession: MediaSessionCompat? = null

    override fun updateNotification(data: NotificationData) {
        if (data !is MixPlaybackState) {
            throw WrongArgumentType("Argument for player notification must be of type MixPlaybackState")
        }
        val playbackState = data
        glide
            .asBitmap()
            .load(playbackState.trackThumbnailUrl)
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
                    notificationBuilder.setLargeIcon(mThumbnailBitmap)
                    notificationManager.notify(
                        NOTIFICATION_ID,
                        notificationBuilder.build()
                    )
                }
            })
        val actionPlayPause = createIntentMaster(playbackState.isMasterPlaying)
        val drawablePlayPause = if (playbackState.isMasterPlaying) {
            R.drawable.ic_pause
        } else {
            R.drawable.ic_play
        }

        if (mMediaSession == null) {
            mMediaSession =
                MediaSessionCompat(
                    context,
                    MEDIA_SESSION_TAG
                )
        }
        notificationBuilder =
            NotificationCompat.Builder(
                context,
                CHANNEL_ID
            ).apply {
                setSmallIcon(R.drawable.ic_note)
                setContentTitle(playbackState.trackTitle)
                setOnlyAlertOnce(true)
                setShowWhen(false)
                addAction(drawablePlayPause, "Play/Pause", actionPlayPause)
                setStyle(
                    androidx.media.app.NotificationCompat.MediaStyle()
                        .setShowActionsInCompactView(0)
                        .setMediaSession(mMediaSession?.sessionToken)
                )
                setContentIntent(createTapIntent())
                setDeleteIntent(createDeleteIntent())
                priority = NotificationCompat.PRIORITY_HIGH
            }
        if (mThumbnailBitmap != null) {
            notificationBuilder.setLargeIcon(mThumbnailBitmap)
        }
        notificationManager.notify(NOTIFICATION_ID, notificationBuilder.build())
    }

    private fun createTapIntent(): PendingIntent? {
        val intent = Intent(context, MainActivity::class.java)
        intent.action = MixPlayerService.ACTION_LAUNCH_PLAYER
        val pendingIntent =
            PendingIntent.getActivity(context, 3, intent, PendingIntent.FLAG_CANCEL_CURRENT)
        return pendingIntent
    }

    private fun createDeleteIntent(): PendingIntent? {
        val intent = Intent(context, MixPlayerService::class.java)
        intent.action =
            MixPlayerService.ACTION_STOP_SERVICE
        val pendingIntent = PendingIntent.getService(
            context,
            2,
            intent,
            PendingIntent.FLAG_CANCEL_CURRENT
        )
        return pendingIntent
    }

    private fun createIntentMaster(isPlaying: Boolean): PendingIntent {
        val intent = Intent(context, MixPlayerService::class.java)
        val action = when (isPlaying) {
            true -> MixPlayerService.ACTION_PAUSE_MASTER
            false -> MixPlayerService.ACTION_PLAY_MASTER
        }

        intent.action = action
        val pendingIntent = PendingIntent.getService(
            context,
            2,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT
        )
        return pendingIntent
    }
}