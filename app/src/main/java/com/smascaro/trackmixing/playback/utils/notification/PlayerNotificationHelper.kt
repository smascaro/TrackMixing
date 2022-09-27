package com.smascaro.trackmixing.playback.utils.notification

import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.os.Build
import android.support.v4.media.MediaMetadataCompat
import android.support.v4.media.session.MediaSessionCompat
import android.support.v4.media.session.PlaybackStateCompat
import android.support.v4.media.session.PlaybackStateCompat.ACTION_PAUSE
import android.support.v4.media.session.PlaybackStateCompat.ACTION_PLAY
import android.support.v4.media.session.PlaybackStateCompat.ACTION_SEEK_TO
import androidx.core.app.NotificationCompat
import com.bumptech.glide.RequestManager
import com.smascaro.trackmixing.base.coroutine.MainCoroutineScope
import com.smascaro.trackmixing.base.exception.WrongArgumentType
import com.smascaro.trackmixing.base.service.NotificationData
import com.smascaro.trackmixing.base.time.asMillis
import com.smascaro.trackmixing.base.utils.NotificationHelper
import com.smascaro.trackmixing.base.utils.loadBitmap
import com.smascaro.trackmixing.R
import com.smascaro.trackmixing.playback.model.MixPlaybackState
import com.smascaro.trackmixing.playback.service.MixPlayerService
import com.smascaro.trackmixing.playback.utils.media.PlaybackSession
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

class PlayerNotificationHelper @Inject constructor(
    private val glide: RequestManager,
    private val ui: com.smascaro.trackmixing.base.coroutine.MainCoroutineScope,
    private val playbackSession: PlaybackSession,
    context: Context
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
        notificationBuilder = createBuilder()
        ui.launch {
            val thumbnailBitmap = glide.loadBitmap(data.trackThumbnailUrl)
            if (thumbnailBitmap != null) {
                mThumbnailBitmap = thumbnailBitmap
                notificationBuilder.setLargeIcon(mThumbnailBitmap)
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
        val mediaMetadataBuilder = buildMediaMetadataBuilder(data)
        val playbackStateBuilder = buildPlaybackStateBuilder(data)
        mMediaSession?.setMetadata(mediaMetadataBuilder.build())
        mMediaSession?.setPlaybackState(playbackStateBuilder.build())
        mMediaSession?.setCallback(object : MediaSessionCompat.Callback() {
            override fun onSeekTo(pos: Long) {
                playbackSession.seek(pos.asMillis().seconds())
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
                    .setShowCancelButton(true)
                    .setCancelButtonIntent(createDeleteIntent())
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

    private fun buildPlaybackStateBuilder(
        data: MixPlaybackState
    ): PlaybackStateCompat.Builder {
        return PlaybackStateCompat.Builder().apply {
            setActions(ACTION_PLAY or ACTION_PAUSE or ACTION_SEEK_TO)
            setState(
                when (data.isMasterPlaying) {
                    true -> PlaybackStateCompat.STATE_PLAYING
                    false -> PlaybackStateCompat.STATE_PAUSED
                },
                data.currentPosition.value,
                1f
            )
        }
    }

    private fun buildMediaMetadataBuilder(data: MixPlaybackState): MediaMetadataCompat.Builder {
        return MediaMetadataCompat.Builder().apply {
            putString(MediaMetadataCompat.METADATA_KEY_ARTIST, data.author)
            putString(MediaMetadataCompat.METADATA_KEY_TITLE, data.trackTitle)
            putString(MediaMetadataCompat.METADATA_KEY_ALBUM_ART_URI, data.trackThumbnailUrl)
            putLong(MediaMetadataCompat.METADATA_KEY_DURATION, data.duration.value)
        }
    }

    private fun createTapIntent(): PendingIntent? {
        val intent = Intent(MixPlayerService.ACTION_LAUNCH_PLAYER)
        val flags = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            PendingIntent.FLAG_CANCEL_CURRENT or PendingIntent.FLAG_IMMUTABLE
        } else {
            PendingIntent.FLAG_CANCEL_CURRENT
        }
        return PendingIntent.getActivity(context, 3, intent, flags)
    }

    private fun createDeleteIntent(): PendingIntent? {
        val intent = Intent(context, MixPlayerService::class.java)
        intent.action =
            MixPlayerService.ACTION_STOP_SERVICE
        val flags = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            PendingIntent.FLAG_CANCEL_CURRENT or PendingIntent.FLAG_IMMUTABLE
        } else {
            PendingIntent.FLAG_CANCEL_CURRENT
        }
        return PendingIntent.getService(
            context,
            2,
            intent,
            flags
        )
    }

    private fun createIntentMaster(isPlaying: Boolean): PendingIntent {
        val intent = Intent(context, MixPlayerService::class.java)
        val action = when (isPlaying) {
            true -> MixPlayerService.ACTION_PAUSE_MASTER
            false -> MixPlayerService.ACTION_PLAY_MASTER
        }

        intent.action = action
        val flags = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        } else {
            PendingIntent.FLAG_UPDATE_CURRENT
        }
        return PendingIntent.getService(
            context,
            2,
            intent,
            flags
        )
    }
}