package com.smascaro.trackmixing.playbackservice.model

import com.smascaro.trackmixing.common.data.model.NotificationData

class MixPlaybackState : NotificationData {
    var trackTitle: String = ""
    var author: String = ""
    var trackThumbnailUrl = ""
    var isMasterPlaying: Boolean = false
}
