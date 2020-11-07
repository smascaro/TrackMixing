package com.smascaro.trackmixing.search.model

data class SearchResult(
    val videoId: String,
    val title: String,
    val author: String,
    val secondsLong: Int,
    val thumbnailUrl: String
)