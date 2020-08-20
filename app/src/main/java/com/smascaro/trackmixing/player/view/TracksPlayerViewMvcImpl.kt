package com.smascaro.trackmixing.player.view

import android.content.Intent
import com.smascaro.trackmixing.playbackservice.utils.PlaybackSession
import com.smascaro.trackmixing.common.data.model.Track
import com.smascaro.trackmixing.common.view.architecture.BaseObservableViewMvc
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