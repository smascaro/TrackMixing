package com.smascaro.trackmixing.tracks

import java.io.Serializable

data class Track(
    val title: String,
    val videoKey: String,
    //val quality: String,
    val thumbnailUrl: String,
    val secondsLong: Int,
    val requestedTimestamp: String
) : Serializable