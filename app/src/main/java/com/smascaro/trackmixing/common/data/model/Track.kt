package com.smascaro.trackmixing.common.data.model

import android.graphics.Color
import java.io.Serializable

data class Track(
    val title: String,
    val author: String,
    val videoKey: String,
    val thumbnailUrl: String,
    val secondsLong: Int,
    val requestedTimestamp: String,
    val downloadPath: String,
    val backgroundColor: Int = Color.BLACK
) : Serializable