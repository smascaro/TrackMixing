package com.smascaro.trackmixing.playbackservice.utils

import com.smascaro.trackmixing.common.data.model.Track
import com.smascaro.trackmixing.common.utils.PlaybackStateManager
import com.smascaro.trackmixing.common.utils.TrackVolumeBundle
import com.smascaro.trackmixing.common.utils.time.Seconds
import com.smascaro.trackmixing.playbackservice.model.TrackInstrument

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
