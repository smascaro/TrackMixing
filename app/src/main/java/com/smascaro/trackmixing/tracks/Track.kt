package com.smascaro.trackmixing.tracks

data class Track(
    val title: String,
    val videoKey: String,
    //val quality: String,
    val thumbnailUrl: String,
    val secondsLong: Int
)