package com.smascaro.trackmixing.playbackservice.controller

import com.smascaro.trackmixing.common.data.model.ForegroundNotification
import com.smascaro.trackmixing.common.data.model.Track
import com.smascaro.trackmixing.common.di.PlayerNotificationHelperImplementation
import com.smascaro.trackmixing.common.di.coroutines.IoCoroutineScope
import com.smascaro.trackmixing.common.utils.*
import com.smascaro.trackmixing.common.utils.PlaybackStateManager.PlaybackState
import com.smascaro.trackmixing.common.utils.ui.NotificationHelper
import com.smascaro.trackmixing.common.view.architecture.BaseObservable
import com.smascaro.trackmixing.playbackservice.model.PlaybackEvent
import com.smascaro.trackmixing.playbackservice.utils.PlaybackHelper
import kotlinx.coroutines.*
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import timber.log.Timber
import javax.inject.Inject
import kotlin.concurrent.thread

class MixPlayerServiceController @Inject constructor(
    val playbackHelper: PlaybackHelper,
    @PlayerNotificationHelperImplementation val notificationHelper: NotificationHelper,
    val playbackStateManager: PlaybackStateManager,
    private val eventBus: EventBus,
    private val io: IoCoroutineScope
) : BaseObservable<MixPlayerServiceController.ServiceActionsDelegate>(),
    PlaybackHelper.Listener {
    interface ServiceActionsDelegate {
        fun onStopService()
        fun onStartForeground(notification: ForegroundNotification)
        fun onStopForeground(removeNotification: Boolean)
    }

    private var timestampUpdateThread: TimestampUpdateThread? = null
    private var reportPlayersOffsetsJob: Job? = null

    fun onCreate() {
        playbackHelper.registerListener(this)
        eventBus.register(this)
        Timber.d("Registered controller to default event bus")
    }

    fun stopService() {
        pauseTimestampThread()
        playbackHelper.finalize()
        getListeners().forEach {
            it.onStopService()
        }
        playbackStateManager.setPlayingStateFlag(PlaybackState.Stopped())
        eventBus.unregister(this)
        Timber.d("Unregistered controller from default event bus")
    }

    fun createOrUpdateNotification() {
        notificationHelper.updateNotification(playbackHelper.getPlaybackState())
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
        stopForeground(false)
    }

    fun onDestroy() {
        playbackHelper.unregisterListener(this)
        playbackStateManager.setPlayingStateFlag(PlaybackState.Stopped())
    }

    fun loadTrack(track: Track) {
        val isInitialized = playbackHelper.isInitialized()
        val isTrackDifferentFromCurrent = isInitialized && track != playbackHelper.getTrack()
        if (!isInitialized || (isInitialized && isTrackDifferentFromCurrent)) {
            playbackHelper.initialize(track)
            playbackStateManager.setCurrentPlayingVolumes(TrackVolumeBundle.getDefault())
            pauseTimestampThread()
        }
    }

    fun playMaster() {
        createOrUpdateNotification()
        startForeground(
            ForegroundNotification(
                PLAYER_NOTIFICATION_ID,
                notificationHelper.getNotification()
            )
        )
        playbackHelper.playMaster()
        startTimestampThread()
        createOrUpdateNotification()
        startForeground(
            ForegroundNotification(
                PLAYER_NOTIFICATION_ID,
                notificationHelper.getNotification()
            )
        )
        io.launch {
            if (playbackStateManager.getCurrentSong() != playbackHelper.getTrack().videoKey) {
                playbackStateManager.setCurrentSongIdPlaying(playbackHelper.getTrack().videoKey)
            }
            playbackStateManager.setPlayingStateFlag(PlaybackState.Playing())
        }
    }

    private fun startTimestampThread() {
        thread {
            timestampUpdateThread = TimestampUpdateThread(playbackHelper, eventBus)
            timestampUpdateThread!!.start()
        }
        reportPlayersOffsetsJob = CoroutineScope(Dispatchers.Main).launch {
            while (true) {
                ensureActive()
                playbackHelper.reportPlayersOffsets()
                delay(1000)
            }
        }
    }

    fun pauseMaster() {
        pauseTimestampThread()
        playbackHelper.pauseMaster()
        stopForeground(false)
        playbackStateManager.setPlayingStateFlag(PlaybackState.Paused())
    }

    private fun pauseTimestampThread() {
        timestampUpdateThread?.cancel()
        timestampUpdateThread = null
        reportPlayersOffsetsJob?.cancel()
        reportPlayersOffsetsJob = null
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
            PLAYER_NOTIFICATION_ACTION_PLAY_MASTER -> playMaster()
            PLAYER_NOTIFICATION_ACTION_PAUSE_MASTER -> pauseMaster()
            PLAYER_NOTIFICATION_ACTION_START_SERVICE -> onStart()
            PLAYER_NOTIFICATION_ACTION_STOP_SERVICE -> stopService()
        }
    }

    private fun onStart() {
        eventBus.register(this)
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
    fun onMessageEvent(pauseMasterEvent: PlaybackEvent.StopMasterEvent) {
        Timber.d("Event of type StopMasterEvent received")
        stopService()
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onMessageEvent(loadTrackEvent: PlaybackEvent.LoadTrackEvent) {
        Timber.d("Event of type LoadTrackEvent received")
        loadTrack(loadTrackEvent.track)
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onMessageEvent(setVolumeMasterEvent: PlaybackEvent.SetVolumeMasterEvent) {
        Timber.d("Event of type SetVolumeMasterEvent received")
        playbackHelper.setMasterVolume(setVolumeMasterEvent.volume)
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onMessageEvent(setVolumeTrackEvent: PlaybackEvent.SetVolumeTrackEvent) {
        Timber.d("Event of type SetVolumeTrackEvent received")
        playbackHelper.setVolume(setVolumeTrackEvent.trackInstrument, setVolumeTrackEvent.volume)
        playbackStateManager.setCurrentPlayingVolumes(playbackHelper.getVolumesBundle())
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onMessageEvent(seekEvent: PlaybackEvent.SeekMaster) {
        Timber.d("Event of type SeekMaster received")
        playbackHelper.seekMaster(seekEvent.seconds)
    }
}