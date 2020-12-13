package com.smascaro.trackmixing.main.components.bottomplayer.controller

import android.content.Intent
import com.smascaro.trackmixing.R
import com.smascaro.trackmixing.base.coroutine.IoCoroutineScope
import com.smascaro.trackmixing.base.coroutine.MainCoroutineScope
import com.smascaro.trackmixing.base.data.model.Track
import com.smascaro.trackmixing.base.utils.navigation.BaseNavigatorController
import com.smascaro.trackmixing.base.utils.navigation.NavigationHelper
import com.smascaro.trackmixing.main.components.bottomplayer.view.BottomPlayerViewMvc
import com.smascaro.trackmixing.playback.service.MixPlayerService
import com.smascaro.trackmixing.playback.utils.media.PlaybackSession
import com.smascaro.trackmixing.playback.utils.state.PlaybackStateManager
import com.smascaro.trackmixing.search.view.SongSearchFragmentDirections
import com.smascaro.trackmixing.trackslist.view.TracksListFragmentDirections
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import timber.log.Timber
import javax.inject.Inject

class BottomPlayerController @Inject constructor(
    private val playbackSession: PlaybackSession,
    private val ui: MainCoroutineScope,
    private val io: IoCoroutineScope,
    navigationHelper: NavigationHelper
) : BaseNavigatorController<BottomPlayerViewMvc>(navigationHelper),
    BottomPlayerViewMvc.Listener {
    private var appearanceBlocked: Boolean = false
    private var currentTrack: Track? = null
    private var currentState: PlaybackStateManager.PlaybackState? = null
    private var openPlayerIntentRequested: Boolean = false
    fun onCreate() {
        ensureViewMvcBound()
        viewMvc.registerListener(this)
        viewMvc.onCreate()
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

    private fun updateUi() {
        if (!appearanceBlocked) {
            viewMvc.showPlayerBar(
                makeBottomPlayerData()
            )
            // viewMvc.bindTrackDuration(currentTrack!!.secondsLong)
            // val volumesBundle = withContext(io.coroutineContext) { playbackSession.getVolumes() }
            // viewMvc.bindVolumes(volumesBundle)
            if (openPlayerIntentRequested) {
                navigateToPlayer()
            }
        }
    }

    private fun makeBottomPlayerData(): com.smascaro.trackmixing.player.model.TrackPlayerData {
        return com.smascaro.trackmixing.player.model.TrackPlayerData(
            currentTrack!!.title,
            currentTrack!!.author,
            currentState!!,
            currentTrack!!.thumbnailUrl
        )
    }

    override fun onBottomPlayerClick() {
        navigateToPlayer()
    }

    private fun navigateToPlayer() {
        if (currentTrack != null) {
            openPlayerIntentRequested = false
            viewMvc.hidePlayerBar()
            when (navigationHelper.currentDestination?.id) {
                R.id.destination_tracks_list -> navigationHelper.navigate(TracksListFragmentDirections.actionDestinationTracksListToPlayerFragment())
                R.id.destination_search -> navigationHelper.navigate(SongSearchFragmentDirections.actionDestinationSearchToDestinationPlayer())
            }
        }
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

    fun handleIntent(intent: Intent?) {
        if (intent?.action == MixPlayerService.ACTION_LAUNCH_PLAYER) {
            openPlayerIntentRequested = true
            navigateToPlayer()
        }
    }

    fun checkIfPlayerShouldShow() {
        updateCurrentPlayingTrack()
    }

    fun blockPlayerAppearance() {
        this.appearanceBlocked = true
    }

    fun unblockPlayerAppearance() {
        this.appearanceBlocked = false
    }
}