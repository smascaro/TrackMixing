package com.smascaro.trackmixing.common.data.network

data class RequestTrackResponseSchema(
    val body: Body,
    val status: Status
) {
    data class Status(
        val code: Int,
        val message: String
    )

    data class Body(
        val track_id: String
    )

}

