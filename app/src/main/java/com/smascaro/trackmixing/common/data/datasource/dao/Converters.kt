package com.smascaro.trackmixing.common.data.datasource.dao

import com.smascaro.trackmixing.common.data.model.DownloadEntity
import com.smascaro.trackmixing.common.data.network.AvailableTracksResponseSchema
import com.smascaro.trackmixing.common.data.model.Track

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