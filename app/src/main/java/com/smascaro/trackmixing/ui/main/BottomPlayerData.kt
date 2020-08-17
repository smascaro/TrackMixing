package com.smascaro.trackmixing.ui.main

import com.smascaro.trackmixing.data.PlaybackStateManager

data class BottomPlayerData(
    val title: String,
    val state: PlaybackStateManager.PlaybackState,
    val thumbnailUrl: String
)