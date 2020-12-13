package com.smascaro.trackmixing.player.controller

import com.smascaro.trackmixing.base.coroutine.IoCoroutineScope
import com.smascaro.trackmixing.base.coroutine.MainCoroutineScope
import com.smascaro.trackmixing.base.data.model.Track
import com.smascaro.trackmixing.base.time.asSeconds
import com.smascaro.trackmixing.base.ui.architecture.controller.BaseController
import com.smascaro.trackmixing.playback.utils.media.PlaybackSession
import com.smascaro.trackmixing.playback.utils.state.PlaybackStateManager
import com.smascaro.trackmixing.player.view.TrackPlayerViewMvc
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import timber.log.Timber
import javax.inject.Inject

class TrackPlayerController @Inject constructor(
    private val eventBus: EventBus,
    private val playbackSession: PlaybackSession,
    private val ui: MainCoroutineScope,
    private val io: IoCoroutineScope
) : BaseController<TrackPlayerViewMvc>(),
    TrackPlayerViewMvc.Listener {
    private var currentTrack: Track? = null
    private var currentState: PlaybackStateManager.PlaybackState? = null

    fun onStart() {
        viewMvc.registerListener(this)
        eventBus.register(this)
        updateCurrentPlayingTrack()
    }

    fun onStop() {
        dispose()
    }

    private fun updateCurrentPlayingTrack() = ui.launch {
        currentState = playbackSession.getState()
        if (currentState is PlaybackStateManager.PlaybackState.Playing || currentState is PlaybackStateManager.PlaybackState.Paused) {
            currentTrack = withContext(io.coroutineContext) { playbackSession.getTrack() }
            when (currentState) {
                is PlaybackStateManager.PlaybackState.Playing -> viewMvc.showPauseButton()
                is PlaybackStateManager.PlaybackState.Paused -> viewMvc.showPlayButton()
            }
            updateUi()
        }
    }

    private suspend fun updateUi() {
        viewMvc.bindTrackTitle(currentTrack!!.title)
        viewMvc.bindTrackDuration(currentTrack!!.secondsLong)
        val volumesBundle = withContext(io.coroutineContext) { playbackSession.getVolumes() }
        viewMvc.bindVolumes(volumesBundle)
    }

    override fun onActionButtonClicked() {
        ui.launch {
            val currentState = playbackSession.getState()
            if (currentState is PlaybackStateManager.PlaybackState.Playing) {
                playbackSession.pause()
                Timber.d("Sent a PauseMasterEvent")
            } else if (currentState is PlaybackStateManager.PlaybackState.Paused) {
                playbackSession.play()
                Timber.d("Sent a PlayMasterEvent")
            }
        }
    }

    override fun onSeekRequestEvent(progress: Int) {
        playbackSession.seek(progress.asSeconds())
    }

    override fun onPlayerStateChanged() {
        updateCurrentPlayingTrack()
    }

    override fun onTrackVolumeChanged(
        trackInstrument: com.smascaro.trackmixing.playback.model.TrackInstrument,
        volume: Int
    ) {
        playbackSession.setTrackVolume(trackInstrument, volume)
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onMessageEvent(event: com.smascaro.trackmixing.playback.model.TimestampChangedEvent) =
        handleTimestampChanged(event.newTimestamp.value.toInt(), event.totalLength.value.toInt())

    private fun handleTimestampChanged(newTimestamp: Int, totalLength: Int) {
        Timber.d("Received timestamp event: $newTimestamp / $totalLength")
        viewMvc.updateTimestamp(newTimestamp, totalLength)
    }

    override fun dispose() {
        viewMvc.unregisterListener(this)
        eventBus.unregister(this)
    }
}
