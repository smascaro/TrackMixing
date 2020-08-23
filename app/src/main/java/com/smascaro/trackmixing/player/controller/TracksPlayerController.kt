package com.smascaro.trackmixing.player.controller

import com.smascaro.trackmixing.common.controller.BaseController
import com.smascaro.trackmixing.common.data.model.Track
import com.smascaro.trackmixing.common.error.NoLoadedTrackException
import com.smascaro.trackmixing.playbackservice.model.TrackInstrument
import com.smascaro.trackmixing.playbackservice.utils.PlaybackSession
import com.smascaro.trackmixing.player.view.TracksPlayerViewMvc
import timber.log.Timber
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
    }

    fun onCreate() {
        ensureViewMvcBound()
        loadTrack()
        viewMvc.registerListener(this)
    }

    private fun isServiceStarted(): Boolean {
        return isServiceStarted
    }

    fun playMaster() {
        if (isServiceStarted()) {
            playbackSession.play()
        } else {
            Timber.w("Can't play master because service is not connected yet.")
        }
    }

    override fun onServiceConnected() {
        loadTrack()
    }

    override fun onTrackVolumeChanged(trackInstrument: TrackInstrument, volume: Int) {
        playbackSession.setTrackVolume(trackInstrument, volume)
    }
}