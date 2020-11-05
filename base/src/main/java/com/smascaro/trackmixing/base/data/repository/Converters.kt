package com.smascaro.trackmixing.base.data.repository

import com.smascaro.trackmixing.base.data.model.DownloadEntity
import com.smascaro.trackmixing.base.data.model.Track
import com.smascaro.trackmixing.base.network.node.model.AvailableTracksResponseSchema
import com.smascaro.trackmixing.base.time.asSeconds

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