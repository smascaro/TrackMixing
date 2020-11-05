package com.smascaro.trackmixing.base.model

import android.graphics.Color
import com.smascaro.trackmixing.base.time.Seconds
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