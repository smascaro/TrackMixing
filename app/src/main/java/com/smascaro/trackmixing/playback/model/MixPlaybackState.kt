package com.smascaro.trackmixing.playback.model

import com.smascaro.trackmixing.base.service.NotificationData
import com.smascaro.trackmixing.base.time.Milliseconds

data class MixPlaybackState(
    var trackTitle: String = "",
    var author: String = "",
    var trackThumbnailUrl: String = "",
    var isMasterPlaying: Boolean = false,
    var duration: Milliseconds = Milliseconds.of(0),
    var currentPosition: Milliseconds = Milliseconds.of(0)
) : NotificationData