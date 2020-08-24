package com.smascaro.trackmixing.player.controller

import com.smascaro.trackmixing.common.controller.BaseController
import com.smascaro.trackmixing.common.data.model.Track
import com.smascaro.trackmixing.common.error.NoLoadedTrackException
import com.smascaro.trackmixing.common.utils.PlaybackStateManager
import com.smascaro.trackmixing.playbackservice.model.PlaybackEvent
import com.smascaro.trackmixing.playbackservice.model.TrackInstrument
import com.smascaro.trackmixing.playbackservice.utils.PlaybackSession
import com.smascaro.trackmixing.player.view.TracksPlayerViewMvc
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import javax.inject.Inject

class TracksPlayerController @Inject constructor(
    private val playbackSession: PlaybackSession
) :
    BaseController<TracksPlayerViewMvc>(),
    TracksPlayerViewMvc.Listener {

    private lateinit var mTrack: Track
    private var isServiceStarted: Boolean = false
    fun bindTrack(track: Track) {
        mTrack = track
    }

    fun loadTrack() {
        if (!this::mTrack.isInitialized) {
            throw NoLoadedTrackException()
        }
        isServiceStarted = playbackSession.startPlayback(mTrack)
    }

    fun onDestroy() {
        viewMvc.unregisterListener(this)
        viewMvc.onDestroy()
        EventBus.getDefault().unregister(this)
    }

    fun onCreate() {
        ensureViewMvcBound()
        initializeActionButton()
        viewMvc.bindVolumes(
            hashMapOf(
                TrackInstrument.VOCALS to 100,
                TrackInstrument.OTHER to 100,
                TrackInstrument.BASS to 100,
                TrackInstrument.DRUMS to 100
            )
        )
        viewMvc.registerListener(this)
        EventBus.getDefault().register(this)
    }

    private fun initializeActionButton() {
        when (playbackSession.getState()) {
            is PlaybackStateManager.PlaybackState.Playing -> viewMvc.showPauseButton()
            is PlaybackStateManager.PlaybackState.Paused -> viewMvc.showPlayButton()
        }
    }

    private fun isServiceStarted(): Boolean {
        return isServiceStarted
    }

    fun playMaster() {
        if (isServiceStarted()) {
            playbackSession.play()
        } else {
            isServiceStarted = playbackSession.startPlayback(mTrack)
        }
    }

    override fun onServiceConnected() {
        loadTrack()
    }

    override fun onTrackVolumeChanged(trackInstrument: TrackInstrument, volume: Int) {
        playbackSession.setTrackVolume(trackInstrument, volume)
    }

    override fun onActionButtonClicked() {
        updateActionButton()
    }

    private fun updateActionButton() {
        when (playbackSession.getState()) {
            is PlaybackStateManager.PlaybackState.Playing -> handleActionButtonOnPlayingState()
            is PlaybackStateManager.PlaybackState.Paused -> handleActionButtonOnPausedState()
        }
    }

    private fun handleActionButtonOnPlayingState() {
        playbackSession.pause()
        viewMvc.showPlayButton()
    }

    private fun handleActionButtonOnPausedState() {
        playbackSession.play()
        viewMvc.showPauseButton()
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onMessageEvent(event: PlaybackEvent.StateChanged) {
        when (event.newState) {
            is PlaybackStateManager.PlaybackState.Playing -> viewMvc.showPauseButton()
            is PlaybackStateManager.PlaybackState.Paused -> viewMvc.showPlayButton()
        }
    }
}