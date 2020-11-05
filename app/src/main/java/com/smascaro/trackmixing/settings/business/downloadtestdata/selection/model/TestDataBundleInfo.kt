package com.smascaro.trackmixing.settings.business.downloadtestdata.selection.model

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

fun Double.round(decimals: Int): Double {
    var multiplier = 1.0
    repeat(decimals) { multiplier *= 10 }
    return Math.round(this * multiplier) / multiplier
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