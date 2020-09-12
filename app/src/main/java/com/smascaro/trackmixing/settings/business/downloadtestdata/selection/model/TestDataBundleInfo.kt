package com.smascaro.trackmixing.settings.business.downloadtestdata.selection.model

data class TestDataBundleInfo(
    val title: String,
    val author: String,
    val duration: String,
    val urlResourceId: String,
    val thumbnailUrl: String,
    val videoKey: String,
    val size: Int,
    var isPresentInDatabase: Boolean = false
)

fun Double.round(decimals: Int): Double {
    var multiplier = 1.0
    repeat(decimals) { multiplier *= 10 }
    return Math.round(this * multiplier) / multiplier
}

val Int.asMB: String
    get() = "${(this.toDouble() / (1000 * 1000)).round(1)}MB"