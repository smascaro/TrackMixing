package com.smascaro.trackmixing.player.model

import com.smascaro.trackmixing.playback.utils.state.PlaybackStateManager

data class TrackPlayerData(
    val title: String,
    val author: String,
    val state: PlaybackStateManager.PlaybackState,
    val thumbnailUrl: String
)