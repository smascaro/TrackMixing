package com.smascaro.trackmixing.base.network.node.model

data class FetchProgressResponseSchema(
    val body: Body,
    val status: Status
) {
    data class Body(
        val finishedTimestamp: String?,
        val id: Int,
        val status_key: Int,
        val playedCount: Int,
        val progress: Int,
        val requestedTimestamp: String?,
        val secondsLong: Int,
        val status: Int,
        val status_message: String?,
        val thumbnailUrl: String?,
        val title: String?,
        val status_code: String?,
        val videoId: String?
    )

    data class Status(
        val code: Int,
        val message: String
    )
}