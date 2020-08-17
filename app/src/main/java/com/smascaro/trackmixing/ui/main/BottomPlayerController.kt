package com.smascaro.trackmixing.ui.main

import com.smascaro.trackmixing.data.DownloadsDao
import com.smascaro.trackmixing.data.PlaybackStateManager
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import javax.inject.Inject

class BottomPlayerController @Inject constructor(
    private val playbackStateManager: PlaybackStateManager,
    private val downloadsDao: DownloadsDao
) {
    private lateinit var viewMvc: BottomPlayerViewMvc

    fun bindViewMvc(viewMvc: BottomPlayerViewMvc) {
        this.viewMvc = viewMvc
    }

    fun onCreate() = runBlocking {
        val playingState = playbackStateManager.getPlayingState()
        if (playingState is PlaybackStateManager.PlaybackState.Playing || playingState is PlaybackStateManager.PlaybackState.Paused) {
            val songId = playbackStateManager.getCurrentSong()
            launch(Dispatchers.Default) {
                val foundTracks = downloadsDao.get(songId)
                if (foundTracks.isNotEmpty()) {
                    viewMvc.showPlayerBar(
                        BottomPlayerData(
                            foundTracks.first().title,
                            playingState,
                            foundTracks.first().thumbnailUrl
                        )
                    )
                }
            }
        } else {
//            viewMvc.hidePlayerBar()
        }
    }
}
