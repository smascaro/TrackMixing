package com.smascaro.trackmixing.playbackservice.utils

import com.smascaro.trackmixing.common.utils.time.Milliseconds
import com.smascaro.trackmixing.common.utils.time.Seconds
import com.smascaro.trackmixing.playbackservice.model.TrackInstrument

interface PlayerActions {
    fun play()
    fun play(instrument: TrackInstrument)
    fun pause()
    fun pause(instrument: TrackInstrument)
    fun seek(newPosition: Seconds)
    fun seekMillis(newPositionMillis: Milliseconds)
    fun getVolume(instrument: TrackInstrument): Float
    fun setVolume(volume: Int, instrument: TrackInstrument? = null)
    fun getCurrentPosition(): Milliseconds
    fun isCompleted(instrument: TrackInstrument): Boolean
    fun isReadyToPlay(): Boolean
}
