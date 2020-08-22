package com.smascaro.trackmixing.main.controller

import com.smascaro.trackmixing.common.controller.BaseController
import com.smascaro.trackmixing.common.data.datasource.repository.TracksRepository
import com.smascaro.trackmixing.common.data.datasource.repository.toModel
import com.smascaro.trackmixing.common.data.model.Track
import com.smascaro.trackmixing.common.utils.PlaybackStateManager
import com.smascaro.trackmixing.main.model.BottomPlayerData
import com.smascaro.trackmixing.main.view.BottomPlayerViewMvc
import com.smascaro.trackmixing.playbackservice.model.PlaybackEvent
import kotlinx.coroutines.runBlocking
import org.greenrobot.eventbus.EventBus
import timber.log.Timber
import javax.inject.Inject

class BottomPlayerController @Inject constructor(
    private val playbackStateManager: PlaybackStateManager,
    private val tracksRepository: TracksRepository
) : BaseController<BottomPlayerViewMvc>(),
    BottomPlayerViewMvc.Listener {

    private var currentTrack: Track? = null

    fun onCreate() {
        ensureViewMvcBound()
        viewMvc.registerListener(this)
        updateCurrentPlayingTrack()
    }

    private fun updateCurrentPlayingTrack() =
        runBlocking {
            val playingState = playbackStateManager.getPlayingState()
            if (playingState is PlaybackStateManager.PlaybackState.Playing || playingState is PlaybackStateManager.PlaybackState.Paused) {
                val songId = playbackStateManager.getCurrentSong()
                currentTrack = tracksRepository.get(songId).toModel()
                when (playingState) {
                    is PlaybackStateManager.PlaybackState.Playing -> viewMvc.showPauseButton()
                    is PlaybackStateManager.PlaybackState.Paused -> viewMvc.showPlayButton()
                }
                viewMvc.showPlayerBar(
                    BottomPlayerData(
                        currentTrack!!.title,
                        playingState,
                        currentTrack!!.thumbnailUrl
                    )
                )
            } else if (playingState is PlaybackStateManager.PlaybackState.Stopped) {
                viewMvc.hidePlayerBar()
            }
        }

    fun onDestroy() {
        viewMvc.unregisterListener(this)
    }

    override fun onLayoutClick() {
        if (currentTrack != null) {
            viewMvc.navigateToPlayer(currentTrack!!)
        }
    }

    override fun onActionButtonClicked() {
        val currentState = playbackStateManager.getPlayingState()
        if (currentState is PlaybackStateManager.PlaybackState.Playing) {
            EventBus.getDefault().post(PlaybackEvent.PauseMasterEvent())
            Timber.d("Sent a PauseMasterEvent")
        } else if (currentState is PlaybackStateManager.PlaybackState.Paused) {
            EventBus.getDefault().post(PlaybackEvent.PlayMasterEvent())
            Timber.d("Sent a PlayMasterEvent")
        }
    }

    override fun onPlayerStateChanged() {
        updateCurrentPlayingTrack()
    }
}