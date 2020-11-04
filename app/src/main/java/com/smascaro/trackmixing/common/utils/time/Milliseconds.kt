package com.smascaro.trackmixing.common.utils.time

fun Long.asMillis() = Milliseconds.of(this)
fun Int.asMillis() = Milliseconds.of(this.toLong())
data class Milliseconds(override val value: Long) : TimeUnit {
    companion object {
        fun of(value: Long) = Milliseconds(value)
    }

    override fun seconds(): Seconds = (value / 1000).asSeconds()

    override fun millis(): Milliseconds = this
}