package com.smascaro.trackmixing.base.utils

fun Double.round(decimals: Int): Double {
    var multiplier = 1.0
    repeat(decimals) { multiplier *= 10 }
    return Math.round(this * multiplier) / multiplier
}

val Int.asKB: String
    get() = "${(this.toDouble() / (1000)).round(1)}KB"
val Long.asKB: String
    get() = "${(this.toDouble() / (1000)).round(1)}KB"
val Int.asMB: String
    get() = "${(this.toDouble() / (1000 * 1000)).round(1)}MB"
val Long.asMB: String
    get() = "${(this.toDouble() / (1000 * 1000)).round(1)}MB"
val Int.asGB: String
    get() = "${(this.toDouble() / (1000 * 1000 * 1000)).round(1)}GB"
val Long.asGB: String
    get() = "${(this.toDouble() / (1000 * 1000 * 1000)).round(1)}GB"