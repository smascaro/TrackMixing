package com.smascaro.trackmixing.common.data.datasource.repository

import com.smascaro.trackmixing.common.data.model.DownloadEntity
import com.smascaro.trackmixing.common.data.model.Track
import com.smascaro.trackmixing.common.data.network.AvailableTracksResponseSchema

fun AvailableTracksResponseSchema.Item.toModel(): Track {
    return Track(
        title,
        videoId,
        thumbnailUrl ?: "",
        secondsLong,
        this.requestedTimestamp,
        ""
    )
}

fun DownloadEntity.toModel(): Track {
    return Track(
        title,
        sourceVideoKey,
        thumbnailUrl,
        secondsLong,
        downloadTimestamp,
        downloadPath
    )
}