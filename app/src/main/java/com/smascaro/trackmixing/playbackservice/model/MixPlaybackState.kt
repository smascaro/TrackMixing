package com.smascaro.trackmixing.playbackservice.model

import com.smascaro.trackmixing.common.data.model.NotificationData

data class MixPlaybackState(
    var trackTitle: String = "",
    var author: String = "",
    var trackThumbnailUrl: String = "",
    var isMasterPlaying: Boolean = false,
    var duration: Long = 0,
    var currentPosition: Long = 0
) : NotificationData