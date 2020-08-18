package com.smascaro.trackmixing.service

import com.smascaro.trackmixing.common.*
import com.smascaro.trackmixing.data.PlaybackStateManager
import com.smascaro.trackmixing.data.PlaybackStateManager.PlaybackState
import com.smascaro.trackmixing.service.events.PauseMasterEvent
import com.smascaro.trackmixing.service.events.PlayMasterEvent
import com.smascaro.trackmixing.service.events.StartServiceEvent
import com.smascaro.trackmixing.tracks.Track
import com.smascaro.trackmixing.ui.common.BaseObservable
import com.smascaro.trackmixing.ui.notification.NotificationHelper
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import timber.log.Timber
import javax.inject.Inject

class MixPlayerServiceController @Inject constructor(
    val playbackHelper: PlaybackHelper,
    val notificationHelper: NotificationHelper,
    val playbackStateManager: PlaybackStateManager
) : BaseObservable<MixPlayerServiceController.ServiceActionsDelegate>(), PlaybackHelper.Listener {
    interface ServiceActionsDelegate {
        fun onStopService()
        fun onStartForeground(notification: ForegroundNotification)
        fun onStopForeground(removeNotification: Boolean)
    }

    fun onCreate() {
        playbackHelper.registerListener(this)
        EventBus.getDefault().register(this)
    }

    fun stopService() {
        playbackHelper.finalize()
        getListeners().forEach {
            it.onStopService()
        }
        playbackStateManager.setPlayingStateFlag(PlaybackState.Stopped())
        EventBus.getDefault().unregister(this)
    }

    fun createOrUpdateNotification() {
        notificationHelper.updateForegroundNotification(playbackHelper.getPlaybackState())
    }

    override fun onInitializationFinished() {
        notificationHelper.createNotificationChannel()
        createOrUpdateNotification()
    }

    override fun onMediaStateChange() {
        createOrUpdateNotification()
    }

    override fun onSongFinished() {
        playbackStateManager.setPlayingStateFlag(PlaybackState.Paused())
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
        if (playbackStateManager.getCurrentSong() != playbackHelper.getTrack().videoKey) {
            playbackStateManager.setCurrentSongIdPlaying(playbackHelper.getTrack().videoKey)
        }
        playbackStateManager.setPlayingStateFlag(PlaybackState.Playing())
    }

    fun pauseMaster() {
        playbackHelper.pauseMaster()
        stopForeground(false)
        playbackStateManager.setPlayingStateFlag(PlaybackState.Paused())
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

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onMessageEvent(startServiceEvent: StartServiceEvent) {
        Timber.d("Event of type StartServiceEvent received")
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onMessageEvent(playMasterEvent: PlayMasterEvent) {
        Timber.d("Event of type PlayMasterEvent received")
        playMaster()
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onMessageEvent(pauseMasterEvent: PauseMasterEvent) {
        Timber.d("Event of type PauseMasterEvent received")
        pauseMaster()
    }
}