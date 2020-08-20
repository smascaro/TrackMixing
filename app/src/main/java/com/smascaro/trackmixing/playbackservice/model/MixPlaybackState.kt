package com.smascaro.trackmixing.playbackservice.model

import com.smascaro.trackmixing.common.data.model.NotificationData

class MixPlaybackState : NotificationData {
    var trackTitle: String = ""
    var trackThumbnailUrl = ""
    var isMasterPlaying: Boolean = false
    var isVocalsPlaying: Boolean = false
    var isOtherPlaying: Boolean = false
    var isBassPlaying: Boolean = false
    var isDrumsPlaying: Boolean = false
}
