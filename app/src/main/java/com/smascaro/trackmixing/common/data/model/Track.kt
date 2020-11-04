package com.smascaro.trackmixing.common.data.model

import android.graphics.Color
import com.smascaro.trackmixing.common.utils.time.Seconds
import java.io.Serializable

data class Track(
    val title: String,
    val author: String,
    val videoKey: String,
    val thumbnailUrl: String,
    val secondsLong: Seconds,
    val requestedTimestamp: Long,
    val downloadPath: String,
    val backgroundColor: Int = Color.BLACK
) : Serializable