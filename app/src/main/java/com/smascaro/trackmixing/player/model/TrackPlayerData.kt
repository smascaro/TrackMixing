package com.smascaro.trackmixing.player.model

import com.smascaro.trackmixing.playback.utils.PlaybackStateManager

data class TrackPlayerData(
    val title: String,
    val author: String,
    val state: com.smascaro.trackmixing.playback.utils.PlaybackStateManager.PlaybackState,
    val thumbnailUrl: String
)