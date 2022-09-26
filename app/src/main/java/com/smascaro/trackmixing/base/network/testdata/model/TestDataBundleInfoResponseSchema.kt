package com.smascaro.trackmixing.base.network.testdata.model

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

