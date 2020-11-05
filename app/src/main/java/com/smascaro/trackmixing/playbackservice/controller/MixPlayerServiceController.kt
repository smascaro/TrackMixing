package com.smascaro.trackmixing.playbackservice.controller

import android.os.Bundle
import com.smascaro.trackmixing.base.coroutine.IoCoroutineScope
import com.smascaro.trackmixing.base.coroutine.MainCoroutineScope
import com.smascaro.trackmixing.base.model.Track
import com.smascaro.trackmixing.base.time.asSeconds
import com.smascaro.trackmixing.common.data.model.ForegroundNotification
import com.smascaro.trackmixing.common.di.PlayerNotificationHelperImplementation
import com.smascaro.trackmixing.common.utils.PlaybackStateManager
import com.smascaro.trackmixing.common.utils.PlaybackStateManager.PlaybackState
import com.smascaro.trackmixing.common.utils.TrackVolumeBundle
import com.smascaro.trackmixing.common.utils.ui.NotificationHelper
import com.smascaro.trackmixing.playbackservice.MixPlayerService
import com.smascaro.trackmixing.playbackservice.model.TrackInstrument
import com.smascaro.trackmixing.playbackservice.utils.BandPlaybackHelper
import com.smascaro.trackmixing.playbackservice.utils.PlayerNotificationHelper
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.ensureActive
import kotlinx.coroutines.launch
import org.greenrobot.eventbus.EventBus
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
) : ServiceCallbackHandler(),
    BandPlaybackHelper.Listener {
    data class ActionArgs(val bundle: Bundle?)

    private var timestampUpdateThread: TimestampUpdateThread? = null
    private var reportPlayersOffsetsJob: Job? = null

    // region Lifecycle
    fun onCreate() {
        bandPlaybackHelper.registerListener(this)
    }

    fun onDestroy() {
        bandPlaybackHelper.unregisterListener(this)
        playbackStateManager.setPlayingStateFlag(PlaybackState.Stopped())
    }
    // endregion

    // region BandPlaybackHelper events
    override fun onInitializationFinished() {
        notificationHelper.createNotificationChannel()
        updateNotification()
    }

    override fun onMediaStateChange() {
        ui.launch {
            updateNotification()
        }
    }

    override fun onSongFinished() {
        playbackStateManager.setPlayingStateFlag(PlaybackState.Paused())
        stopForeground(false)
    }
    // endregion

    // region Actions
    fun executeAction(action: String?, args: ActionArgs? = null) {
        when (action) {
            MixPlayerService.ACTION_LOAD_TRACK -> handleLoadTrack(args)
            MixPlayerService.ACTION_PLAY_MASTER -> playMaster()
            MixPlayerService.ACTION_PAUSE_MASTER -> pauseMaster()
            MixPlayerService.ACTION_INSTRUMENT_VOLUME -> changeVolumeInstrument(args)
            MixPlayerService.ACTION_SEEK -> seek(args)
            MixPlayerService.ACTION_STOP_SERVICE -> stopService()
        }
    }

    private fun handleLoadTrack(args: ActionArgs?) {
        if (validateLoadTrackExtras(args)) {
            val track =
                args!!.bundle!!.get(MixPlayerService.EXTRA_LOAD_TRACK_PARAM_KEY) as Track
            loadTrack(track)
            if (shouldStartPlaying(args.bundle!!)) {
                playMaster()
            }
        } else {
            Timber.e("Load track action called but no track argument supplied.")
        }
    }

    private fun validateLoadTrackExtras(args: ActionArgs?) =
        args?.bundle != null && args.bundle.containsKey(MixPlayerService.EXTRA_LOAD_TRACK_PARAM_KEY)

    private fun shouldStartPlaying(extras: Bundle): Boolean =
        extras.getBoolean(MixPlayerService.EXTRA_START_PLAYING_PARAM_KEY, false)

    private fun loadTrack(track: Track) {
        val isInitialized = bandPlaybackHelper.isInitialized()
        val isTrackDifferentFromCurrent = isInitialized && track != bandPlaybackHelper.getTrack()
        if (!isInitialized || (isInitialized && isTrackDifferentFromCurrent)) {
            bandPlaybackHelper.initialize(track)
            playbackStateManager.setCurrentPlayingVolumes(TrackVolumeBundle.getDefault())
            pauseTimestampThread()
        }
    }

    private fun playMaster() {
        bandPlaybackHelper.playMaster()
        startTimestampThread()
        updateNotification()
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

    private fun startForeground(foregroundNotification: ForegroundNotification) {
        handleStartForeground(foregroundNotification)
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

    private fun pauseMaster() {
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

    private fun stopForeground(removeNotification: Boolean) = handleStopForeground(removeNotification)

    private fun seek(args: ActionArgs?) {
        if (validateSeekExtras(args)) {
            val seconds = args!!.bundle!!.getInt(MixPlayerService.EXTRA_SEEK_SECONDS_PARAM_KEY)
            bandPlaybackHelper.seekMaster(seconds.asSeconds())
        }
    }

    private fun validateSeekExtras(args: ActionArgs?): Boolean {
        return args?.bundle != null &&
            args.bundle.containsKey(MixPlayerService.EXTRA_SEEK_SECONDS_PARAM_KEY)
    }

    private fun changeVolumeInstrument(args: ActionArgs?) {
        if (validateVolumeChangeExtras(args)) {
            val instrument =
                args!!.bundle!!.getSerializable(MixPlayerService.EXTRA_VOLUME_INSTRUMENT_PARAM_KEY) as TrackInstrument
            val volume = args.bundle!!.getInt(MixPlayerService.EXTRA_VOLUME_VALUE_PARAM_KEY)
            bandPlaybackHelper.setVolume(
                instrument,
                volume
            )
            playbackStateManager.setCurrentPlayingVolumes(bandPlaybackHelper.getVolumesBundle())
        } else {
            Timber.e("There are missing extras in ChangeVolume action.")
        }
    }

    private fun validateVolumeChangeExtras(args: ActionArgs?): Boolean {
        return args?.bundle != null &&
            args.bundle.containsKey(MixPlayerService.EXTRA_VOLUME_INSTRUMENT_PARAM_KEY)
            && args.bundle.containsKey(MixPlayerService.EXTRA_VOLUME_VALUE_PARAM_KEY)
    }

    private fun stopService() {
        pauseTimestampThread()
        bandPlaybackHelper.finalize()
        handleStopService()
        playbackStateManager.setPlayingStateFlag(PlaybackState.Stopped())
    }

    private fun updateNotification() {
        notificationHelper.updateNotification(bandPlaybackHelper.getPlaybackState())
    }
    // endregion
}