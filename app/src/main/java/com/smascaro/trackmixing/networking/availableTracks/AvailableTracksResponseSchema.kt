package com.smascaro.trackmixing.networking.availableTracks

data class AvailableTracksResponseSchema(

    val items: List<Item>,
    val result: Result
) {
    data class Result(
        val code: Int,
        val message: String
    )

    data class Item(
        val finishedTimestamp: Any?,
        val id: Int,
        val playedCount: Int,
        val progress: Int,
        val requestedTimestamp: String,
        val secondsLong: Int,
        val status: Int,
        val thumbnailUrl: String?,
        val title: String,
        val videoId: String
    )
}