package com.smascaro.trackmixing.main.components.bottomplayer.model

import com.smascaro.trackmixing.common.utils.PlaybackStateManager

data class BottomPlayerData(
    val title: String,
    val author: String,
    val state: PlaybackStateManager.PlaybackState,
    val thumbnailUrl: String
)