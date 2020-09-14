package com.smascaro.trackmixing.common.utils

import com.smascaro.trackmixing.settings.business.downloadtestdata.selection.model.round

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