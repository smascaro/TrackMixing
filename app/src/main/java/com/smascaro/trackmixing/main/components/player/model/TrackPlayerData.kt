package com.smascaro.trackmixing.main.components.player.model

import com.smascaro.trackmixing.common.utils.PlaybackStateManager

data class TrackPlayerData(
    val title: String,
    val author: String,
    val state: PlaybackStateManager.PlaybackState,
    val thumbnailUrl: String
)