package com.smascaro.trackmixing.service

import com.smascaro.trackmixing.tracks.Track

class MixPlaybackState {
    var trackTitle: String = ""
    var trackThumbnailUrl = ""
    var isMasterPlaying: Boolean = false
    var isVocalsPlaying: Boolean = false
    var isOtherPlaying: Boolean = false
    var isBassPlaying: Boolean = false
    var isDrumsPlaying: Boolean = false
}
