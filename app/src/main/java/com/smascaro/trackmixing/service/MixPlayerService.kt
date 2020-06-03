package com.smascaro.trackmixing.service

import android.content.Intent
import android.os.IBinder
import com.smascaro.trackmixing.common.NOTIFICATION_ACTION_PAUSE_MASTER
import com.smascaro.trackmixing.common.NOTIFICATION_ACTION_PLAY_MASTER
import com.smascaro.trackmixing.common.NOTIFICATION_ID
import com.smascaro.trackmixing.service.common.BaseService
import com.smascaro.trackmixing.tracks.Track
import com.smascaro.trackmixing.ui.notification.NotificationHelper

class MixPlayerService : BaseService(), PlayingHelper.Listener {
    inner class Binder : android.os.Binder() {
        fun getService(): MixPlayerService {
            return this@MixPlayerService
        }

        fun loadTrack(track: Track) {
            this@MixPlayerService.mPlayingHelper.initialize(track)
        }

        fun play() {
            this@MixPlayerService.mPlayingHelper.playMaster()
        }

        fun pause() {
            this@MixPlayerService.mPlayingHelper.pauseMaster()
        }
    }

    private lateinit var mPlayingHelper: PlayingHelper
    private lateinit var mNotificationHelper: NotificationHelper
    private val mBinder = Binder()
    override fun onBind(intent: Intent?): IBinder? {
        return mBinder
    }

    override fun onCreate() {
        super.onCreate()
        mPlayingHelper = getCompositionRoot().getPlayingHelper()
        mPlayingHelper.registerListener(this)
        mNotificationHelper = getCompositionRoot().getNotificationHelper()

    }

    override fun onDestroy() {
        super.onDestroy()
        mPlayingHelper.unregisterListener(this)
    }

    fun createOrUpdateNotification() {
        mNotificationHelper.createNotification(
            mPlayingHelper.getTrack(),
            mPlayingHelper.isPlaying(),
            true,
            true,
            true,
            true
        )
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        if (intent != null) {
            val action = intent.action
            when (action) {
                NOTIFICATION_ACTION_PLAY_MASTER -> mPlayingHelper.playMaster()
                NOTIFICATION_ACTION_PAUSE_MASTER -> mPlayingHelper.pauseMaster()
            }
        }

        return START_NOT_STICKY
    }

    override fun onInitializtionFinished() {
        mNotificationHelper.createNotificationChannel()
        createOrUpdateNotification()
        startForeground(NOTIFICATION_ID, mNotificationHelper.getNotification())
    }

    override fun onMediaStateChange() {
        createOrUpdateNotification()
    }
}