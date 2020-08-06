package com.smascaro.trackmixing.service

import android.content.Intent
import android.os.IBinder
import com.smascaro.trackmixing.common.*
import com.smascaro.trackmixing.service.common.BaseService
import com.smascaro.trackmixing.tracks.Track
import com.smascaro.trackmixing.ui.notification.NotificationHelper

class MixPlayerService : BaseService(), PlaybackHelper.Listener {
    inner class Binder : android.os.Binder() {
        fun getService(): MixPlayerService {
            return this@MixPlayerService
        }

        fun loadTrack(track: Track) {
            if (track != mPlaybackHelper.getTrack()) {
                mPlaybackHelper.initialize(track)
            }
        }

        fun play() {
            this@MixPlayerService.mPlaybackHelper.playMaster()
        }

        fun pause() {
            this@MixPlayerService.mPlaybackHelper.pauseMaster()
        }

    }


    private lateinit var mPlaybackHelper: PlaybackHelper
    private lateinit var mNotificationHelper: NotificationHelper
    private val mBinder = Binder()
    override fun onBind(intent: Intent?): IBinder? {
        return mBinder
    }

    override fun onCreate() {
        super.onCreate()
        mPlaybackHelper = getCompositionRoot().getPlayingHelper()
        mPlaybackHelper.registerListener(this)
        mNotificationHelper = getCompositionRoot().getNotificationHelper()

    }

    fun stopService() {
        mPlaybackHelper.finalize()
        stopForeground(true)
        stopSelf()
    }

    override fun onDestroy() {
        super.onDestroy()
        mPlaybackHelper.unregisterListener(this)
    }

    fun createOrUpdateNotification() {
        mNotificationHelper.createNotification(
            mPlaybackHelper.getTrack()!!,
            mPlaybackHelper.isPlaying(),
            mPlaybackHelper.isInstrumentPlaying(TrackInstrument.VOCALS),
            mPlaybackHelper.isInstrumentPlaying(TrackInstrument.OTHER),
            mPlaybackHelper.isInstrumentPlaying(TrackInstrument.BASS),
            mPlaybackHelper.isInstrumentPlaying(TrackInstrument.DRUMS)
        )
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        if (intent != null) {
            val action = intent.action
            when (action) {
                NOTIFICATION_ACTION_PLAY_MASTER -> playMaster()
                NOTIFICATION_ACTION_PAUSE_MASTER -> pauseMaster()
                NOTIFICATION_ACTION_MUTE_VOCALS -> mPlaybackHelper.muteTrack(TrackInstrument.VOCALS)
                NOTIFICATION_ACTION_UNMUTE_VOCALS -> mPlaybackHelper.unmuteTrack(TrackInstrument.VOCALS)
                NOTIFICATION_ACTION_MUTE_OTHER -> mPlaybackHelper.muteTrack(TrackInstrument.OTHER)
                NOTIFICATION_ACTION_UNMUTE_OTHER -> mPlaybackHelper.unmuteTrack(TrackInstrument.OTHER)
                NOTIFICATION_ACTION_MUTE_BASS -> mPlaybackHelper.muteTrack(TrackInstrument.BASS)
                NOTIFICATION_ACTION_UNMUTE_BASS -> mPlaybackHelper.unmuteTrack(TrackInstrument.BASS)
                NOTIFICATION_ACTION_MUTE_DRUMS -> mPlaybackHelper.muteTrack(TrackInstrument.DRUMS)
                NOTIFICATION_ACTION_UNMUTE_DRUMS -> mPlaybackHelper.unmuteTrack(TrackInstrument.DRUMS)
                NOTIFICATION_ACTION_STOP_SERVICE -> {
                    stopService()
                }
            }
        }

        return START_NOT_STICKY
    }

    private fun playMaster() {
        createOrUpdateNotification()
        startForeground(NOTIFICATION_ID, mNotificationHelper.getNotification())
        mPlaybackHelper.playMaster()
        createOrUpdateNotification()
        startForeground(NOTIFICATION_ID, mNotificationHelper.getNotification())
    }

    private fun pauseMaster() {
        mPlaybackHelper.pauseMaster()
        stopForeground(false)
        stopSelf()
    }

    override fun onInitializationFinished() {
        mNotificationHelper.createNotificationChannel()
        createOrUpdateNotification()
        startForeground(NOTIFICATION_ID, mNotificationHelper.getNotification())
    }

    override fun onMediaStateChange() {
        createOrUpdateNotification()
    }

    override fun onSongFinished() {
        stopService()
    }
}