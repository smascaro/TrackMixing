package com.smascaro.trackmixing.settings.business.downloadtestdata.selection.model

import android.os.Parcelable
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
