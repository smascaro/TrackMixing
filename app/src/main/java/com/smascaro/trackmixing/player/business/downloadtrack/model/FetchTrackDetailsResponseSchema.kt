package com.smascaro.trackmixing.player.business.downloadtrack.model

import com.smascaro.trackmixing.common.data.model.Track

data class FetchTrackDetailsResponseSchema(
    val body: Body,
    val status: Status
) {
    data class Body(
        val finishedTimestamp: String,
        val id: Int,
        val playedCount: Int,
        val progress: Int,
        val requestedTimestamp: String,
        val secondsLong: Int,
        val status: Int,
        val status_code: String,
        val status_key: Int,
        val status_message: String,
        val thumbnailUrl: String,
        val title: String,
        val videoId: String
    )

    data class Status(
        val code: Int,
        val message: String
    )
}

fun FetchTrackDetailsResponseSchema.toModel(): Track = Track(
    body.title,
    body.videoId,
    body.thumbnailUrl,
    body.secondsLong,
    body.requestedTimestamp,
    ""
)