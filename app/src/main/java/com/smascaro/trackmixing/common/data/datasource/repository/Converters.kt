package com.smascaro.trackmixing.common.data.datasource.repository

import com.smascaro.trackmixing.common.data.model.DownloadEntity
import com.smascaro.trackmixing.common.data.model.Track
import com.smascaro.trackmixing.common.data.network.AvailableTracksResponseSchema
import com.smascaro.trackmixing.common.utils.time.asSeconds

fun AvailableTracksResponseSchema.Item.toModel(): Track {
    return Track(
        title,
        "",
        videoId,
        thumbnailUrl ?: "",
        secondsLong.asSeconds(),
        0,
        ""
    )
}

fun DownloadEntity.toModel(): Track {
    return Track(
        title,
        author,
        sourceVideoKey,
        thumbnailUrl,
        secondsLong.asSeconds(),
        downloadTimestamp,
        downloadPath,
        backgroundColor
    )
}