package com.smascaro.trackmixing.common.data.model

import java.io.Serializable

data class Track(
    val title: String,
    val videoKey: String,
    //val quality: String,
    val thumbnailUrl: String,
    val secondsLong: Int,
    val requestedTimestamp: String,
    val downloadPath: String
) : Serializable