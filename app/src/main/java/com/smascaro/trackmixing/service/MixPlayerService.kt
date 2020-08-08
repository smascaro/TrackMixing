package com.smascaro.trackmixing.service

import android.content.Intent
import android.os.IBinder
import com.smascaro.trackmixing.common.*
import com.smascaro.trackmixing.service.common.BaseService
import com.smascaro.trackmixing.tracks.Track
import com.smascaro.trackmixing.ui.notification.NotificationHelper
import timber.log.Timber

class MixPlayerService : BaseService(), PlaybackHelper.Listener {

    private lateinit var mPlaybackHelper: PlaybackHelper
    private lateinit var mNotificationHelper: NotificationHelper

    override fun onBind(intent: Intent?): IBinder? {
        return null
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
        mNotificationHelper.updateForegroundNotification(mPlaybackHelper.getPlaybackState())
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
                NOTIFICATION_ACTION_LOAD_TRACK -> loadTrackFromIntent(intent, action)
                NOTIFICATION_ACTION_STOP_SERVICE -> stopService()
            }
        }

        return START_STICKY
    }

    private fun loadTrackFromIntent(intent: Intent, action: String?) {
        if (intent.extras != null &&
            intent.extras!!.containsKey(NOTIFICATION_EXTRA_LOAD_TRACK_PARAM_KEY)
        ) {
            val track = intent.extras!!.get(NOTIFICATION_EXTRA_LOAD_TRACK_PARAM_KEY) as Track
            loadTrack(track)
        } else {
            Timber.w("$action action called but no track parameter supplied")
        }
    }

    private fun loadTrack(track: Track) {
        val isInitialized = mPlaybackHelper.isInitialized()
        val isTrackDifferentFromCurrent = isInitialized && track != mPlaybackHelper.getTrack()
        if (!isInitialized || (isInitialized && isTrackDifferentFromCurrent)) {
            mPlaybackHelper.initialize(track)
        }
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
        //No action
    }
}