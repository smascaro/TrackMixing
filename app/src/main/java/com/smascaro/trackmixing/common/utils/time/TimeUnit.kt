package com.smascaro.trackmixing.common.utils.time

import java.io.Serializable

interface TimeUnit : Serializable {
    val value: Long
    fun seconds(): Seconds
    fun millis(): Milliseconds
}