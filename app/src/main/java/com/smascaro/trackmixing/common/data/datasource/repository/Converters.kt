package com.smascaro.trackmixing.common.data.datasource.repository

import com.smascaro.trackmixing.base.model.Track
import com.smascaro.trackmixing.base.network.node.model.AvailableTracksResponseSchema
import com.smascaro.trackmixing.base.time.asSeconds
import com.smascaro.trackmixing.common.data.model.DownloadEntity

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