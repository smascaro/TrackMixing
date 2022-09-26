package com.smascaro.trackmixing.base.time

fun Long.asSeconds() = Seconds.of(this)
fun Int.asSeconds() = Seconds.of(this.toLong())
data class Seconds(override val value: Long) : TimeUnit {
    companion object {
        fun of(value: Long) = Seconds(value)
    }

    override fun seconds(): Seconds = this

    override fun millis(): Milliseconds = (value * 1000).asMillis()
}