package com.smascaro.trackmixing.playbackservice.utils

import android.content.Context
import com.smascaro.trackmixing.base.model.Track
import com.smascaro.trackmixing.base.time.Seconds
import com.smascaro.trackmixing.common.utils.PlaybackStateManager
import com.smascaro.trackmixing.common.utils.TrackVolumeBundle
import com.smascaro.trackmixing.playbackservice.MixPlayerService
import com.smascaro.trackmixing.playbackservice.model.TrackInstrument
import javax.inject.Inject

class PlaybackSessionImpl @Inject constructor(
    private val context: Context,
    private val playbackStateManager: PlaybackStateManager
) :
    PlaybackSession {
    private var isServiceStarted: Boolean = false

    override fun isSessionInitialized() = isServiceStarted

    override fun startPlayback(track: Track): Boolean {
        isServiceStarted = MixPlayerService.start(context, track)
        return isSessionInitialized()
    }

    override fun stopPlayback() {
        MixPlayerService.stop(context)
    }

    override fun play() {
        MixPlayerService.play(context)
    }

    override fun pause() {
        MixPlayerService.pause(context)
    }

    override fun seek(seconds: Seconds) {
        MixPlayerService.seek(context, seconds)
    }

    override fun setTrackVolume(trackInstrument: TrackInstrument, volume: Int) {
        MixPlayerService.setVolume(context, trackInstrument, volume)
    }

    override suspend fun getState(): PlaybackStateManager.PlaybackState {
        return playbackStateManager.getPlayingState()
    }

    override suspend fun getTrack(): Track {
        return playbackStateManager.getCurrentTrack()
    }

    override suspend fun getVolumes(): TrackVolumeBundle {
        return playbackStateManager.getCurrentPlayingVolumes()
    }
}