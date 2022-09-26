package com.smascaro.trackmixing.settings.testdata.selection.model

import android.os.Parcelable
import com.smascaro.trackmixing.base.network.testdata.model.TestDataBundleInfoResponseSchema
import com.smascaro.trackmixing.base.time.TimeHelper
import kotlinx.android.parcel.Parcelize

@Parcelize
data class TestDataBundleInfo(
    val title: String,
    val author: String,
    val duration: String,
    val resourceFilename: String,
    val thumbnailUrl: String,
    val videoKey: String,
    val size: Int,
    var isPresentInDatabase: Boolean = false
) : Parcelable


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