package com.smascaro.trackmixing.settings.business.downloadtestdata.selection.model

import com.smascaro.trackmixing.common.utils.time.TimeHelper

data class TestDataBundleInfoResponseSchema(
    val files: List<File>,
    val files_count: Int
) {
    data class File(
        val author: String,
        val downloadPath: String?,
        val download_size: Int,
        val requestedTimestamp: String?,
        val s3_filename: String,
        val secondsLong: Int,
        val thumbnailUrl: String,
        val title: String,
        val videoKey: String
    )
}

fun TestDataBundleInfoResponseSchema.File.toModel(): TestDataBundleInfo {
    return TestDataBundleInfo(
        title,
        author,
        TimeHelper.fromSeconds(secondsLong.toLong()).toStringRepresentation(),
        s3_filename,
        thumbnailUrl,
        videoKey,
        download_size
    )
}

fun TestDataBundleInfoResponseSchema.toModelList(): List<TestDataBundleInfo> {
    return this.files.map { it.toModel() }
}