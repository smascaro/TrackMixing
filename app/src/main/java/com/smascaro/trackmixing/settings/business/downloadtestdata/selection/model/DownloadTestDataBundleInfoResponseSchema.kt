package com.smascaro.trackmixing.settings.business.downloadtestdata.selection.model

import com.smascaro.trackmixing.common.data.model.Track

data class DownloadTestDataBundleInfoResponseSchema(
    val files: List<File>,
    val files_count: Int,
    val url: String
) {
    data class File(
        val author: String,
        val downloadPath: String?,
        val requestedTimestamp: String?,
        val secondsLong: Int,
        val thumbnailUrl: String,
        val title: String,
        val videoKey: String
    )
}

fun DownloadTestDataBundleInfoResponseSchema.File.toTrackModel(): Track {
    return Track(
        title,
        author,
        videoKey,
        thumbnailUrl,
        secondsLong,
        requestedTimestamp ?: "",
        downloadPath ?: ""
    )
}