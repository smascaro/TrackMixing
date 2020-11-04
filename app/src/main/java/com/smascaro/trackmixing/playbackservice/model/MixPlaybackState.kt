package com.smascaro.trackmixing.playbackservice.model

import com.smascaro.trackmixing.common.data.model.NotificationData
import com.smascaro.trackmixing.common.utils.time.Milliseconds

data class MixPlaybackState(
    var trackTitle: String = "",
    var author: String = "",
    var trackThumbnailUrl: String = "",
    var isMasterPlaying: Boolean = false,
    var duration: Milliseconds = Milliseconds.of(0),
    var currentPosition: Milliseconds = Milliseconds.of(0)
) : NotificationData