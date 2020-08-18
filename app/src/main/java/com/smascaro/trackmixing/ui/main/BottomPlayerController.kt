package com.smascaro.trackmixing.ui.main

import com.smascaro.trackmixing.data.DownloadsDao
import com.smascaro.trackmixing.data.PlaybackStateManager
import com.smascaro.trackmixing.data.toModel
import com.smascaro.trackmixing.tracks.Track
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext
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

    fun onCreate() {
        updateCurrentPlayingTrack()
    }

    private fun updateCurrentPlayingTrack() = runBlocking {
        val playingState = playbackStateManager.getPlayingState()
        if (playingState is PlaybackStateManager.PlaybackState.Playing || playingState is PlaybackStateManager.PlaybackState.Paused) {
            val songId = playbackStateManager.getCurrentSong()
            currentTrack = fetchTrackByVideoId(songId)
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

    private suspend fun fetchTrackByVideoId(videoId: String): Track = withContext(Dispatchers.IO) {
        val foundTracks = downloadsDao.get(videoId)
        if (foundTracks.isNotEmpty()) {
            foundTracks.first().toModel()
        } else {
            throw Exception("Track with Id $videoId not found in database")
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

    override fun onPlayerStateChanged() {
        updateCurrentPlayingTrack()
    }
}
