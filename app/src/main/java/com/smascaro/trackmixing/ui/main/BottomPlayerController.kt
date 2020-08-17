package com.smascaro.trackmixing.ui.main

import com.smascaro.trackmixing.data.DownloadsDao
import com.smascaro.trackmixing.data.PlaybackStateManager
import com.smascaro.trackmixing.data.toModel
import com.smascaro.trackmixing.tracks.Track
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import javax.inject.Inject

class BottomPlayerController @Inject constructor(
    private val playbackStateManager: PlaybackStateManager,
    private val downloadsDao: DownloadsDao
) : BottomPlayerViewMvc.Listener {
    private lateinit var viewMvc: BottomPlayerViewMvc
    private var currentTrack: Track? = null
    fun bindViewMvc(viewMvc: BottomPlayerViewMvc) {
        this.viewMvc = viewMvc
        this.viewMvc.registerListener(this)
    }

    fun onCreate() = runBlocking {
        val playingState = playbackStateManager.getPlayingState()
        if (playingState is PlaybackStateManager.PlaybackState.Playing || playingState is PlaybackStateManager.PlaybackState.Paused) {
            val songId = playbackStateManager.getCurrentSong()
            launch(Dispatchers.Default) {
                val foundTracks = downloadsDao.get(songId)
                if (foundTracks.isNotEmpty()) {
                    currentTrack = foundTracks.first().toModel()
                    viewMvc.showPlayerBar(
                        BottomPlayerData(
                            currentTrack!!.title,
                            playingState,
                            currentTrack!!.thumbnailUrl
                        )
                    )
                }
            }
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
//        TODO("Not yet implemented")
    }
}
