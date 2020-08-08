package com.smascaro.trackmixing.service

import com.smascaro.trackmixing.common.*
import com.smascaro.trackmixing.tracks.Track
import com.smascaro.trackmixing.ui.common.BaseObservable
import com.smascaro.trackmixing.ui.notification.NotificationHelper
import javax.inject.Inject

class MixPlayerServiceController @Inject constructor(
    val playbackHelper: PlaybackHelper,
    val notificationHelper: NotificationHelper
) : BaseObservable<MixPlayerServiceController.ServiceActionsDelegate>(), PlaybackHelper.Listener {
    interface ServiceActionsDelegate {
        fun onStopService()
        fun onStartForeground(notification: ForegroundNotification)
        fun onStopForeground(removeNotification: Boolean)
    }

    fun onCreate() {
        playbackHelper.registerListener(this)
    }

    fun stopService() {
        playbackHelper.finalize()
        getListeners().forEach {
            it.onStopForeground(true)
            it.onStopService()
        }
    }

    fun createOrUpdateNotification() {
        notificationHelper.updateForegroundNotification(playbackHelper.getPlaybackState())
    }

    override fun onInitializationFinished() {
        notificationHelper.createNotificationChannel()
        createOrUpdateNotification()
        startForeground(
            ForegroundNotification(
                NOTIFICATION_ID,
                notificationHelper.getNotification()
            )
        )
    }

    override fun onMediaStateChange() {
        createOrUpdateNotification()
    }

    override fun onSongFinished() {
        //No action
    }

    fun onDestroy() {
        playbackHelper.unregisterListener(this)
    }

    fun loadTrack(track: Track) {
        val isInitialized = playbackHelper.isInitialized()
        val isTrackDifferentFromCurrent = isInitialized && track != playbackHelper.getTrack()
        if (!isInitialized || (isInitialized && isTrackDifferentFromCurrent)) {
            playbackHelper.initialize(track)
        }
    }

    fun playMaster() {
        createOrUpdateNotification()
        startForeground(
            ForegroundNotification(
                NOTIFICATION_ID,
                notificationHelper.getNotification()
            )
        )
        playbackHelper.playMaster()
        createOrUpdateNotification()
        startForeground(
            ForegroundNotification(
                NOTIFICATION_ID,
                notificationHelper.getNotification()
            )
        )
    }

    fun pauseMaster() {
        playbackHelper.pauseMaster()
        stopForeground(false)
    }

    private fun startForeground(foregroundNotification: ForegroundNotification) {
        getListeners().forEach {
            it.onStartForeground(foregroundNotification)
        }
    }

    private fun stopForeground(removeNotification: Boolean) {
        getListeners().forEach {
            it.onStopForeground(removeNotification)
        }
    }

    fun executeAction(action: String?) {
        when (action) {
            NOTIFICATION_ACTION_PLAY_MASTER -> playMaster()
            NOTIFICATION_ACTION_PAUSE_MASTER -> pauseMaster()
            NOTIFICATION_ACTION_MUTE_VOCALS -> playbackHelper.muteTrack(TrackInstrument.VOCALS)
            NOTIFICATION_ACTION_UNMUTE_VOCALS -> playbackHelper.unmuteTrack(TrackInstrument.VOCALS)
            NOTIFICATION_ACTION_MUTE_OTHER -> playbackHelper.muteTrack(TrackInstrument.OTHER)
            NOTIFICATION_ACTION_UNMUTE_OTHER -> playbackHelper.unmuteTrack(TrackInstrument.OTHER)
            NOTIFICATION_ACTION_MUTE_BASS -> playbackHelper.muteTrack(TrackInstrument.BASS)
            NOTIFICATION_ACTION_UNMUTE_BASS -> playbackHelper.unmuteTrack(TrackInstrument.BASS)
            NOTIFICATION_ACTION_MUTE_DRUMS -> playbackHelper.muteTrack(TrackInstrument.DRUMS)
            NOTIFICATION_ACTION_UNMUTE_DRUMS -> playbackHelper.unmuteTrack(TrackInstrument.DRUMS)
            NOTIFICATION_ACTION_STOP_SERVICE -> stopService()
        }
    }
}