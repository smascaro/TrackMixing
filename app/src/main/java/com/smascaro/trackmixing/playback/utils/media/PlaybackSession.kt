package com.smascaro.trackmixing.playback.utils.media

import com.smascaro.trackmixing.base.data.model.Track
import com.smascaro.trackmixing.base.time.Seconds
import com.smascaro.trackmixing.playback.model.TrackInstrument
import com.smascaro.trackmixing.playback.model.TrackVolumeBundle
import com.smascaro.trackmixing.playback.utils.state.PlaybackStateManager

interface PlaybackSession {
    fun isSessionInitialized(): Boolean
    fun startPlayback(track: com.smascaro.trackmixing.base.data.model.Track): Boolean
    fun stopPlayback()
    fun play()
    fun pause()
    fun seek(seconds: Seconds)
    fun setTrackVolume(trackInstrument: TrackInstrument, volume: Int)
    suspend fun getState(): PlaybackStateManager.PlaybackState
    suspend fun getTrack(): com.smascaro.trackmixing.base.data.model.Track
    suspend fun getVolumes(): TrackVolumeBundle
}
