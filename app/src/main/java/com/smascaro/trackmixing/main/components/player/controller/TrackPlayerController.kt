package com.smascaro.trackmixing.main.components.player.controller

import android.content.Intent
import com.smascaro.trackmixing.common.controller.BaseController
import com.smascaro.trackmixing.common.data.datasource.repository.TracksRepository
import com.smascaro.trackmixing.common.data.datasource.repository.toModel
import com.smascaro.trackmixing.common.data.model.Track
import com.smascaro.trackmixing.common.di.coroutines.IoCoroutineScope
import com.smascaro.trackmixing.common.di.coroutines.MainCoroutineScope
import com.smascaro.trackmixing.common.utils.PLAYER_NOTIFICATION_ACTION_LAUNCH_PLAYER
import com.smascaro.trackmixing.common.utils.PlaybackStateManager
import com.smascaro.trackmixing.main.components.player.model.TrackPlayerData
import com.smascaro.trackmixing.main.components.player.view.TrackPlayerViewMvc
import com.smascaro.trackmixing.playbackservice.model.PlaybackEvent
import com.smascaro.trackmixing.playbackservice.model.TrackInstrument
import com.smascaro.trackmixing.playbackservice.utils.PlaybackSession
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import timber.log.Timber
import javax.inject.Inject

class TrackPlayerController @Inject constructor(
    private val playbackStateManager: PlaybackStateManager,
    private val tracksRepository: TracksRepository,
    private val eventBus: EventBus,
    private val playbackSession: PlaybackSession,
    private val ui: MainCoroutineScope,
    private val io: IoCoroutineScope
) : BaseController<TrackPlayerViewMvc>(),
    TrackPlayerViewMvc.Listener {
    private var currentTrack: Track? = null
    private var currentState: PlaybackStateManager.PlaybackState? = null
    private var openPlayerIntentRequested: Boolean = false
    fun onCreate() {
        ensureViewMvcBound()
        viewMvc.registerListener(this)
        eventBus.register(this)
        viewMvc.onCreate()
    }

    private fun updateCurrentPlayingTrack() = ui.launch {
        currentState = playbackStateManager.getPlayingState()
        if (currentState is PlaybackStateManager.PlaybackState.Playing || currentState is PlaybackStateManager.PlaybackState.Paused) {
            val songId = withContext(io.coroutineContext) { playbackStateManager.getCurrentSong() }
            currentTrack = tracksRepository.get(songId).toModel()
            when (currentState) {
                is PlaybackStateManager.PlaybackState.Playing -> viewMvc.showPauseButton()
                is PlaybackStateManager.PlaybackState.Paused -> viewMvc.showPlayButton()
            }
            updateUi()
        }
    }

    private fun updateUi() {
        viewMvc.showPlayerBar(
            makeBottomPlayerData()
        )

        viewMvc.bindTrackDuration(currentTrack!!.secondsLong)
        viewMvc.bindVolumes(playbackSession.getVolumes())
        if (openPlayerIntentRequested) {
            navigateToPlayer()
        }
    }

    private fun makeBottomPlayerData(): TrackPlayerData {
        return TrackPlayerData(
            currentTrack!!.title,
            currentTrack!!.author,
            currentState!!,
            currentTrack!!.thumbnailUrl
        )
    }

    override fun onLayoutClick() {
        navigateToPlayer()
    }

    private fun navigateToPlayer() {
        if (currentTrack != null) {
            openPlayerIntentRequested = false
            viewMvc.openPlayer()
        }
    }

    override fun onActionButtonClicked() {
        val currentState = playbackStateManager.getPlayingState()
        if (currentState is PlaybackStateManager.PlaybackState.Playing) {
            eventBus.post(PlaybackEvent.PauseMasterEvent())
            Timber.d("Sent a PauseMasterEvent")
        } else if (currentState is PlaybackStateManager.PlaybackState.Paused) {
            eventBus.post(PlaybackEvent.PlayMasterEvent())
            Timber.d("Sent a PlayMasterEvent")
        }
    }

    override fun onSeekRequestEvent(progress: Int) {
        playbackSession.seek(progress)
    }

    override fun onPlayerStateChanged() {
        updateCurrentPlayingTrack()
    }

    override fun onServiceRunningCheck(running: Boolean) {
        if (running) {
            updateCurrentPlayingTrack()
        }
    }

    override fun onPlayerSwipedOut() {
        playbackSession.stopPlayback()
    }

    override fun onTrackVolumeChanged(trackInstrument: TrackInstrument, volume: Int) {
        playbackSession.setTrackVolume(trackInstrument, volume)
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onMessageEvent(event: PlaybackEvent.TimestampChanged) =
        handleTimestampChanged(event.newTimestamp, event.totalLength)

    private fun handleTimestampChanged(newTimestamp: Int, totalLength: Int) {
        Timber.d("Received timestamp event: $newTimestamp / $totalLength")
        viewMvc.updateTimestamp(newTimestamp, totalLength)
    }

    fun handleIntent(intent: Intent?) {
        if (intent?.action == PLAYER_NOTIFICATION_ACTION_LAUNCH_PLAYER) {
            openPlayerIntentRequested = true
            navigateToPlayer()
        }
    }

    override fun dispose() {
        viewMvc.unregisterListener(this)
        eventBus.unregister(this)
    }
}
