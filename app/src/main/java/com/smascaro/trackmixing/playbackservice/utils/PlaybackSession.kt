package com.smascaro.trackmixing.playbackservice.utils

import com.smascaro.trackmixing.common.data.model.Track

interface PlaybackSession {
    fun isSessionInitialized(): Boolean
    fun startPlayback(track: Track): Boolean
    fun stopPlayback()
    fun play()
    fun pause()
    fun seek(seconds: Int)
}
