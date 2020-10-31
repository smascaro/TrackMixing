package com.smascaro.trackmixing.playbackservice.controller

import android.os.Bundle
import com.smascaro.trackmixing.common.data.model.ForegroundNotification
import com.smascaro.trackmixing.common.data.model.Track
import com.smascaro.trackmixing.common.di.PlayerNotificationHelperImplementation
import com.smascaro.trackmixing.common.di.coroutines.IoCoroutineScope
import com.smascaro.trackmixing.common.di.coroutines.MainCoroutineScope
import com.smascaro.trackmixing.common.utils.PlaybackStateManager
import com.smascaro.trackmixing.common.utils.PlaybackStateManager.PlaybackState
import com.smascaro.trackmixing.common.utils.TrackVolumeBundle
import com.smascaro.trackmixing.common.utils.ui.NotificationHelper
import com.smascaro.trackmixing.common.view.architecture.BaseObservable
import com.smascaro.trackmixing.playbackservice.model.PlaybackEvent
import com.smascaro.trackmixing.playbackservice.utils.BandPlaybackHelper
import com.smascaro.trackmixing.playbackservice.utils.PlayerNotificationHelper
import kotlinx.coroutines.*
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import timber.log.Timber
import javax.inject.Inject
import kotlin.concurrent.thread

class MixPlayerServiceController @Inject constructor(
    private val bandPlaybackHelper: BandPlaybackHelper,
    @PlayerNotificationHelperImplementation val notificationHelper: NotificationHelper,
    private val playbackStateManager: PlaybackStateManager,
    private val eventBus: EventBus,
    private val io: IoCoroutineScope,
    private val ui: MainCoroutineScope
) : BaseObservable<MixPlayerServiceController.ServiceActionsDelegate>(),
    BandPlaybackHelper.Listener {
    interface ServiceActionsDelegate {
        fun onStopService()
        fun onStartForeground(notification: ForegroundNotification)
        fun onStopForeground(removeNotification: Boolean)
    }

    data class ActionArgs(val bundle: Bundle?)

    private var timestampUpdateThread: TimestampUpdateThread? = null
    private var reportPlayersOffsetsJob: Job? = null

    fun onCreate() {
        bandPlaybackHelper.registerListener(this)
        eventBus.register(this)
        Timber.d("Registered controller to default event bus")
    }

    fun stopService() {
        pauseTimestampThread()
        bandPlaybackHelper.finalize()
        getListeners().forEach {
            it.onStopService()
        }
        playbackStateManager.setPlayingStateFlag(PlaybackState.Stopped())
        eventBus.unregister(this)
        Timber.d("Unregistered controller from default event bus")
    }

    private fun createOrUpdateNotification() {
        notificationHelper.updateNotification(bandPlaybackHelper.getPlaybackState())
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
        bandPlaybackHelper.unregisterListener(this)
        playbackStateManager.setPlayingStateFlag(PlaybackState.Stopped())
    }

    fun loadTrack(track: Track) {
        val isInitialized = bandPlaybackHelper.isInitialized()
        val isTrackDifferentFromCurrent = isInitialized && track != bandPlaybackHelper.getTrack()
        if (!isInitialized || (isInitialized && isTrackDifferentFromCurrent)) {
            bandPlaybackHelper.initialize(track)
            playbackStateManager.setCurrentPlayingVolumes(TrackVolumeBundle.getDefault())
            pauseTimestampThread()
        }
    }

    fun playMaster() {
        createOrUpdateNotification()
        startForeground(
            ForegroundNotification(
                PlayerNotificationHelper.NOTIFICATION_ID,
                notificationHelper.getNotification()
            )
        )
        bandPlaybackHelper.playMaster()
        startTimestampThread()
        createOrUpdateNotification()
        startForeground(
            ForegroundNotification(
                PlayerNotificationHelper.NOTIFICATION_ID,
                notificationHelper.getNotification()
            )
        )
        io.launch {
            if (playbackStateManager.getCurrentSong() != bandPlaybackHelper.getTrack().videoKey) {
                playbackStateManager.setCurrentSongIdPlaying(bandPlaybackHelper.getTrack().videoKey)
            }
            playbackStateManager.setPlayingStateFlag(PlaybackState.Playing())
        }
    }

    private fun startTimestampThread() {
        thread {
            timestampUpdateThread = TimestampUpdateThread(bandPlaybackHelper, eventBus, ui)
            timestampUpdateThread!!.start()
        }
        reportPlayersOffsetsJob = CoroutineScope(Dispatchers.Main).launch {
            while (true) {
                ensureActive()
                bandPlaybackHelper.reportPlayersOffsets()
                delay(1000)
            }
        }
    }

    fun pauseMaster() {
        pauseTimestampThread()
        bandPlaybackHelper.pauseMaster()
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

    fun executeAction(action: String?, args: ActionArgs? = null) {
        when (action) {
            PlayerNotificationHelper.ACTION_LOAD_TRACK -> handleLoadTrack(args)
            PlayerNotificationHelper.ACTION_PLAY_MASTER -> playMaster()
            PlayerNotificationHelper.ACTION_PAUSE_MASTER -> pauseMaster()
            PlayerNotificationHelper.ACTION_START_SERVICE -> onStart()
            PlayerNotificationHelper.ACTION_STOP_SERVICE -> stopService()
        }
    }

    private fun handleLoadTrack(args: ActionArgs?) {
        if (args != null && args.bundle?.containsKey(PlayerNotificationHelper.EXTRA_LOAD_TRACK_PARAM_KEY) == true) {
            val track =
                args.bundle.get(PlayerNotificationHelper.EXTRA_LOAD_TRACK_PARAM_KEY) as Track
            loadTrack(track)
            if (shouldStartPlaying(args.bundle)) {
                playMaster()
            }
        } else {
            Timber.e("Load track action called but no track argument supplied.")
        }
    }

    private fun shouldStartPlaying(extras: Bundle): Boolean =
        extras.containsKey(PlayerNotificationHelper.EXTRA_START_PLAYING_PARAM_KEY) &&
                extras.getBoolean(PlayerNotificationHelper.EXTRA_START_PLAYING_PARAM_KEY)

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
        bandPlaybackHelper.setMasterVolume(setVolumeMasterEvent.volume)
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onMessageEvent(setVolumeTrackEvent: PlaybackEvent.SetVolumeTrackEvent) {
        Timber.d("Event of type SetVolumeTrackEvent received")
        bandPlaybackHelper.setVolume(
            setVolumeTrackEvent.trackInstrument,
            setVolumeTrackEvent.volume
        )
        playbackStateManager.setCurrentPlayingVolumes(bandPlaybackHelper.getVolumesBundle())
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onMessageEvent(seekEvent: PlaybackEvent.SeekMaster) {
        Timber.d("Event of type SeekMaster received")
        bandPlaybackHelper.seekMaster(seekEvent.seconds)
    }
}