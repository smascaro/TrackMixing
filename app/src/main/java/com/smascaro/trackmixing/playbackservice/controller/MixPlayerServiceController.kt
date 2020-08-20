package com.smascaro.trackmixing.playbackservice.controller

import com.smascaro.trackmixing.common.utils.PlaybackStateManager
import com.smascaro.trackmixing.common.utils.PlaybackStateManager.PlaybackState
import com.smascaro.trackmixing.playbackservice.model.PlaybackEvent
import com.smascaro.trackmixing.common.data.model.Track
import com.smascaro.trackmixing.common.utils.*
import com.smascaro.trackmixing.playbackservice.model.ForegroundNotification
import com.smascaro.trackmixing.playbackservice.model.TrackInstrument
import com.smascaro.trackmixing.playbackservice.utils.NotificationHelper
import com.smascaro.trackmixing.playbackservice.utils.PlaybackHelper
import com.smascaro.trackmixing.common.view.architecture.BaseObservable
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import timber.log.Timber
import javax.inject.Inject

class MixPlayerServiceController @Inject constructor(
    val playbackHelper: PlaybackHelper,
    val notificationHelper: NotificationHelper,
    val playbackStateManager: PlaybackStateManager
) : BaseObservable<MixPlayerServiceController.ServiceActionsDelegate>(),
    PlaybackHelper.Listener {
    interface ServiceActionsDelegate {
        fun onStopService()
        fun onStartForeground(notification: ForegroundNotification)
        fun onStopForeground(removeNotification: Boolean)
    }

    fun onCreate() {
        playbackHelper.registerListener(this)
        EventBus.getDefault().register(this)
        Timber.d("Registered controller to default event bus")
    }

    fun stopService() {
        playbackHelper.finalize()
        getListeners().forEach {
            it.onStopService()
        }
        playbackStateManager.setPlayingStateFlag(PlaybackState.Stopped())
        EventBus.getDefault().unregister(this)
        Timber.d("Unregistered controller from default event bus")
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
            NOTIFICATION_ACTION_START_SERVICE -> onStart()
            NOTIFICATION_ACTION_STOP_SERVICE -> stopService()
        }
    }

    private fun onStart() {
        EventBus.getDefault().register(this)
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onMessageEvent(startServiceEvent: PlaybackEvent.StartServiceEvent) {
        Timber.d("Event of type StartServiceEvent received")
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onMessageEvent(playMasterEvent: PlaybackEvent.PlayMasterEvent) {
        Timber.d("Event of type PlayMasterEvent received")
        playMaster()
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onMessageEvent(pauseMasterEvent: PlaybackEvent.PauseMasterEvent) {
        Timber.d("Event of type PauseMasterEvent received")
        pauseMaster()
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onMessageEvent(loadTrackEvent: PlaybackEvent.LoadTrackEvent) {
        Timber.d("Event of type LoadTrackEvent received")
        loadTrack(loadTrackEvent.track)
    }
}