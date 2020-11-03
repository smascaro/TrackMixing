package com.smascaro.trackmixing.playbackservice.utils

import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.support.v4.media.MediaMetadataCompat
import android.support.v4.media.session.MediaSessionCompat
import android.support.v4.media.session.PlaybackStateCompat
import androidx.core.app.NotificationCompat
import com.bumptech.glide.RequestManager
import com.smascaro.trackmixing.R
import com.smascaro.trackmixing.common.data.model.NotificationData
import com.smascaro.trackmixing.common.di.coroutines.MainCoroutineScope
import com.smascaro.trackmixing.common.error.WrongArgumentType
import com.smascaro.trackmixing.common.utils.ui.NotificationHelper
import com.smascaro.trackmixing.common.utils.ui.loadBitmap
import com.smascaro.trackmixing.main.view.MainActivity
import com.smascaro.trackmixing.playbackservice.MixPlayerService
import com.smascaro.trackmixing.playbackservice.model.MixPlaybackState
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

class PlayerNotificationHelper @Inject constructor(
    context: Context,
    val glide: RequestManager,
    private val ui: MainCoroutineScope
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
        Timber.d("Notification data: $data")
        //        glide
//            .asBitmap()
//            .load(playbackState.trackThumbnailUrl)
//            .diskCacheStrategy(DiskCacheStrategy.AUTOMATIC)
//            .into(object : CustomTarget<Bitmap>() {
//                override fun onLoadCleared(placeholder: Drawable?) {
//                    if (mThumbnailBitmap != null) {
//                        mThumbnailBitmap!!.recycle()
//                    }
//                }
//
//                override fun onResourceReady(
//                    resource: Bitmap,
//                    transition: Transition<in Bitmap>?
//                ) {
//                    mThumbnailBitmap = resource
//                    notificationBuilder.setLargeIcon(mThumbnailBitmap)
//                    notificationManager.notify(
//                        NOTIFICATION_ID,
//                        notificationBuilder.build()
//                    )
//                }
//            })
        notificationBuilder = createBuilder()
        ui.launch {
            val thumbnailBitmap = glide.loadBitmap(data.trackThumbnailUrl)
            if (thumbnailBitmap != null) {
                mThumbnailBitmap = thumbnailBitmap
                notificationBuilder.setLargeIcon(mThumbnailBitmap)
//            notificationManager.notify(
//                NOTIFICATION_ID,
//                notificationBuilder.build()
//            )
            }

        }
        val actionPlayPause = createIntentMaster(data.isMasterPlaying)
        val drawablePlayPause = if (data.isMasterPlaying) {
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
        val mediaMetadataBuilder = MediaMetadataCompat.Builder().apply {
            putString(MediaMetadataCompat.METADATA_KEY_ARTIST, data.author)
            putString(MediaMetadataCompat.METADATA_KEY_TITLE, data.trackTitle)
            putString(MediaMetadataCompat.METADATA_KEY_ALBUM_ART_URI, data.trackThumbnailUrl)
            putLong(MediaMetadataCompat.METADATA_KEY_DURATION, data.duration * 1000)
        }
        val playbackStateBuilder = PlaybackStateCompat.Builder().apply {
            setActions(PlaybackStateCompat.ACTION_PLAY_PAUSE or PlaybackStateCompat.ACTION_SEEK_TO)
            setState(
                when (data.isMasterPlaying) {
                    true -> PlaybackStateCompat.STATE_PLAYING
                    false -> PlaybackStateCompat.STATE_PAUSED
                },
                data.currentPosition,
                1f
            )
        }
        mMediaSession?.setMetadata(mediaMetadataBuilder.build())
        mMediaSession?.setPlaybackState(playbackStateBuilder.build())
        mMediaSession?.setCallback(object : MediaSessionCompat.Callback() {
            override fun onPlay() {
                super.onPlay()
                Timber.d("Player notification. Overridden onPlay")
                createIntentMaster(false).send()
            }

            override fun onPause() {
                super.onPause()
                Timber.d("Player notification. Overridden onPause")
                createIntentMaster(true).send()
            }
        })
        notificationBuilder.apply {
            setSmallIcon(R.drawable.ic_note)
            setContentTitle(data.trackTitle)
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