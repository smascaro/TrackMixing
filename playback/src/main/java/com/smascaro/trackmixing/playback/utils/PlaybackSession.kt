package com.smascaro.trackmixing.playback.utils

import com.smascaro.trackmixing.base.data.model.Track
import com.smascaro.trackmixing.base.time.Seconds
import com.smascaro.trackmixing.playback.model.TrackInstrument

interface PlaybackSession {
    fun isSessionInitialized(): Boolean
    fun startPlayback(track: Track): Boolean
    fun stopPlayback()
    fun play()
    fun pause()
    fun seek(seconds: Seconds)
    fun setTrackVolume(trackInstrument: TrackInstrument, volume: Int)
    suspend fun getState(): PlaybackStateManager.PlaybackState
    suspend fun getTrack(): Track
    suspend fun getVolumes(): TrackVolumeBundle
}
