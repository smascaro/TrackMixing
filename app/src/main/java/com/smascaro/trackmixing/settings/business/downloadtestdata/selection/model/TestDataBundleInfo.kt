package com.smascaro.trackmixing.settings.business.downloadtestdata.selection.model

data class TestDataBundleInfo(
    val title: String,
    val author: String,
    val duration: String,
    val urlResourceId: String,
    val thumbnailUrl: String,
    val videoKey: String,
    val size: Int
)