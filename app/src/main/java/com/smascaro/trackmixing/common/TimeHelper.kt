package com.smascaro.trackmixing.common

import org.threeten.bp.Instant
import java.time.LocalDateTime
import java.util.*

fun Int.toMinutesAndSecondsRepresentation(): String {
    var minutes = (this / 60).toInt()
    val hours = (minutes / 60).toInt()
    if (hours > 0) {
        minutes = ((this - 60 * 60 * hours) / 60).toInt()
    }
    val seconds = this - hours * 60 * 60 - minutes * 60
    return if (hours > 0) {
        "$hours:$minutes:$seconds"
    } else {
        "$minutes:$seconds"
    }
}

fun Instant.elapsed(): String {
    return this.toString()
}