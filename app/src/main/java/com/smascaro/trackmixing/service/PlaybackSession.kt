package com.smascaro.trackmixing.service

import com.smascaro.trackmixing.tracks.Track

interface PlaybackSession {
    fun isSessionInitialized(): Boolean
    fun startPlayback(track: Track): Boolean
    fun stopPlayback()
    fun play()
    fun pause()
    fun seek(seconds: Int)
}
