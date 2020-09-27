package com.smascaro.trackmixing.playbackservice.utils

import com.smascaro.trackmixing.playbackservice.model.TrackInstrument

interface PlayerActions {
    fun play()
    fun play(instrument: TrackInstrument)
    fun pause()
    fun pause(instrument: TrackInstrument)
    fun seek(newPosition: Int)
    fun getVolume(instrument: TrackInstrument): Float
    fun setVolume(volume: Int, instrument: TrackInstrument? = null)
    fun getCurrentPosition(): Long
    fun isCompleted(instrument: TrackInstrument): Boolean
    fun isReadyToPlay(): Boolean
}
