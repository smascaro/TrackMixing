package com.smascaro.trackmixing.ui.main

import android.widget.Toast
import com.smascaro.trackmixing.data.PlaybackStateManager
import com.smascaro.trackmixing.data.PlaybackStateManager.PlaybackState
import com.smascaro.trackmixing.ui.common.BaseObservableViewMvc
import javax.inject.Inject

class MainActivityViewMvcImpl @Inject constructor(private val playbackStateManager: PlaybackStateManager) :
    MainActivityViewMvc,
    BaseObservableViewMvc<MainActivityViewMvc.Listener>() {

    override fun showMessage(text: String) {
        Toast.makeText(getContext(), text, Toast.LENGTH_SHORT).show()
    }

    override fun checkPlaybackState() {
        val currentPlaybackState = playbackStateManager.getPlayingState()
        if (currentPlaybackState is PlaybackState.Playing || currentPlaybackState is PlaybackState.Paused) {
            val songId = playbackStateManager.getCurrentSong()
//            if (currentPlaybackState is PlaybackState.Playing) {
//                showMessage("Currently playing song with id $songId")
//            } else {
//                showMessage("Song with id $songId is currently paused")
//            }
//
        } else {
            showMessage("Playback is stopped")
        }
    }

}