package com.smascaro.trackmixing.playbackservice.utils

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
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.bumptech.glide.RequestManager
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.transition.Transition
import com.smascaro.trackmixing.R
import com.smascaro.trackmixing.common.utils.*
import com.smascaro.trackmixing.common.error.NonExistentInstrumentException
import com.smascaro.trackmixing.playbackservice.model.MixPlaybackState
import com.smascaro.trackmixing.playbackservice.MixPlayerService
import com.smascaro.trackmixing.playbackservice.model.TrackInstrument
import javax.inject.Inject

class NotificationHelper @Inject constructor(val mContext: Context, val glide: RequestManager) {
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

    fun getUpdatedNotification(playbackState: MixPlaybackState): Notification {
        updateForegroundNotification(playbackState)
        return getNotification()
    }

    fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val notificationChannel = NotificationChannel(
                NOTIFICATION_CHANNEL_ID,
                "TrackMixing player notification", NotificationManager.IMPORTANCE_LOW
            )
            mNotificationManager.createNotificationChannel(notificationChannel)
        }
    }

    fun updateForegroundNotification(playbackState: MixPlaybackState) {
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
                    mNotificationBuilder.setLargeIcon(mThumbnailBitmap)
                    mNotificationManager.notify(
                        NOTIFICATION_ID,
                        mNotificationBuilder.build()
                    )
                }

            })

        val actionPlayPause = createIntentMaster(playbackState.isMasterPlaying)
        val drawablePlayPause = if (playbackState.isMasterPlaying) {
            R.drawable.ic_pause
        } else {
            R.drawable.ic_play
        }

        val actionVocals =
            createIntentTrack(TrackInstrument.VOCALS, playbackState.isVocalsPlaying)
        val drawableMuteUnmuteVocals =
            getTrackDrawableId(TrackInstrument.VOCALS, playbackState.isVocalsPlaying)

        val actionOther = createIntentTrack(TrackInstrument.OTHER, playbackState.isOtherPlaying)
        val drawableMuteUnmuteOther =
            getTrackDrawableId(TrackInstrument.OTHER, playbackState.isOtherPlaying)

        val actionBass = createIntentTrack(TrackInstrument.BASS, playbackState.isBassPlaying)
        val drawableMuteUnmuteBass =
            getTrackDrawableId(TrackInstrument.BASS, playbackState.isBassPlaying)

        val actionDrums = createIntentTrack(TrackInstrument.DRUMS, playbackState.isDrumsPlaying)
        val drawableMuteUnmuteDrums =
            getTrackDrawableId(TrackInstrument.DRUMS, playbackState.isDrumsPlaying)

        if (mMediaSession == null) {
            mMediaSession =
                MediaSessionCompat(
                    mContext,
                    NOTIFICATION_MEDIA_SESSION_TAG
                )
        }
        mNotificationBuilder =
            NotificationCompat.Builder(mContext,
                NOTIFICATION_CHANNEL_ID
            ).apply {
                setSmallIcon(R.drawable.ic_note)
                setContentTitle(playbackState.trackTitle)
                setOnlyAlertOnce(true)
                setShowWhen(false)
                addAction(drawablePlayPause, "Play/Pause", actionPlayPause)
                addAction(drawableMuteUnmuteVocals, "Mute/Unmute vocals", actionVocals)
                addAction(drawableMuteUnmuteOther, "Mute/Unmute guitar", actionOther)
                addAction(drawableMuteUnmuteBass, "Mute/Unmute bass", actionBass)
                addAction(drawableMuteUnmuteDrums, "Mute/Unmute drums", actionDrums)
                setStyle(
                    androidx.media.app.NotificationCompat.MediaStyle()
                        .setShowActionsInCompactView(0)
                        .setMediaSession(mMediaSession?.sessionToken)
                )
                setDeleteIntent(createDeleteIntent())
                priority = NotificationCompat.PRIORITY_HIGH
            }
        if (mThumbnailBitmap != null) {
            mNotificationBuilder.setLargeIcon(mThumbnailBitmap)
        }
        mNotificationManager.notify(NOTIFICATION_ID, mNotificationBuilder.build())
    }

    private fun getTrackDrawableId(
        trackInstrument: TrackInstrument,
        isPlaying: Boolean
    ): Int {
        return if (trackInstrument == TrackInstrument.VOCALS && isPlaying) {
            R.drawable.ic_vocals_mute
        } else if (trackInstrument == TrackInstrument.VOCALS && !isPlaying) {
            R.drawable.ic_vocals_unmute
        } else if (trackInstrument == TrackInstrument.OTHER && isPlaying) {
            R.drawable.ic_guitar_mute
        } else if (trackInstrument == TrackInstrument.OTHER && !isPlaying) {
            R.drawable.ic_guitar_unmute
        } else if (trackInstrument == TrackInstrument.BASS && isPlaying) {
            R.drawable.ic_bass_mute
        } else if (trackInstrument == TrackInstrument.BASS && !isPlaying) {
            R.drawable.ic_bass_unmute
        } else if (trackInstrument == TrackInstrument.DRUMS && isPlaying) {
            R.drawable.ic_drums_mute
        } else if (trackInstrument == TrackInstrument.DRUMS && !isPlaying) {
            R.drawable.ic_drums_unmute
        } else {
            throw NonExistentInstrumentException(
                "Instrument $trackInstrument does not exist or is not supported"
            )
        }
    }

    private fun createDeleteIntent(): PendingIntent? {
        val intent = Intent(mContext, MixPlayerService::class.java)
        intent.action =
            NOTIFICATION_ACTION_STOP_SERVICE
        val pendingIntent = PendingIntent.getService(
            mContext,
            2,
            intent,
            PendingIntent.FLAG_CANCEL_CURRENT
        )
        return pendingIntent
    }

    private fun createIntentTrack(instrument: TrackInstrument, isPlaying: Boolean): PendingIntent {
        val intent = Intent(mContext, MixPlayerService::class.java)
        val action = when (instrument) {
            TrackInstrument.VOCALS -> when (isPlaying) {
                true -> NOTIFICATION_ACTION_MUTE_VOCALS
                false -> NOTIFICATION_ACTION_UNMUTE_VOCALS
            }
            TrackInstrument.OTHER -> when (isPlaying) {
                true -> NOTIFICATION_ACTION_MUTE_OTHER
                false -> NOTIFICATION_ACTION_UNMUTE_OTHER
            }
            TrackInstrument.BASS -> when (isPlaying) {
                true -> NOTIFICATION_ACTION_MUTE_BASS
                false -> NOTIFICATION_ACTION_UNMUTE_BASS
            }
            TrackInstrument.DRUMS -> when (isPlaying) {
                true -> NOTIFICATION_ACTION_MUTE_DRUMS
                false -> NOTIFICATION_ACTION_UNMUTE_DRUMS
            }
        }
        intent.action = action
        val pendingIntent = PendingIntent.getService(
            mContext,
            1,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT
        )
        return pendingIntent
    }

    private fun createIntentMaster(isPlaying: Boolean): PendingIntent {
        val intent = Intent(mContext, MixPlayerService::class.java)
        val action = when (isPlaying) {
            true -> NOTIFICATION_ACTION_PAUSE_MASTER
            false -> NOTIFICATION_ACTION_PLAY_MASTER
        }

        intent.action = action
        val pendingIntent = PendingIntent.getService(
            mContext,
            2,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT
        )
        return pendingIntent
    }
}