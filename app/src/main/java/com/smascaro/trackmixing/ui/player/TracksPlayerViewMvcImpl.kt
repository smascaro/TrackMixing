package com.smascaro.trackmixing.ui.player

import android.content.Intent
import com.smascaro.trackmixing.service.PlaybackSession
import com.smascaro.trackmixing.tracks.Track
import com.smascaro.trackmixing.ui.common.BaseObservableViewMvc
import javax.inject.Inject

class TracksPlayerViewMvcImpl @Inject constructor(
    private val playbackSession: PlaybackSession
) :
    BaseObservableViewMvc<TracksPlayerViewMvc.Listener>(),
    TracksPlayerViewMvc {

    private var mServiceIntent: Intent? = null
    private var mIsServiceStarted: Boolean = false

    override fun isServiceStarted(): Boolean {
        return mIsServiceStarted
    }

    override fun onDestroy() {
        //no action
    }

    override fun loadTrack(track: Track) {
        mIsServiceStarted = playbackSession.startPlayback(track)
    }

    override fun playMaster() {
        playbackSession.play()
    }

    override fun pauseMaster() {
        playbackSession.pause()
    }
}