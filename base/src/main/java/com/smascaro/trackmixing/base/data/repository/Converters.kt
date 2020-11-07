package com.smascaro.trackmixing.base.data.repository

import com.smascaro.trackmixing.base.data.model.DownloadEntity
import com.smascaro.trackmixing.base.data.model.Track
import com.smascaro.trackmixing.base.time.asSeconds

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