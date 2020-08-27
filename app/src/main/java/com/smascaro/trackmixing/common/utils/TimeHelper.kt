package com.smascaro.trackmixing.common.utils

class TimeHelper {
    companion object {
        class Millis(val millis: Long) {
            fun toSeconds(): Long {
                return millis / 1000
            }
        }

        class Seconds(val seconds: Long) {
            fun toStringRepresentation(): String {
                val retHours = seconds / 3600
                val retMinutes = (seconds - 3600 * retHours) / 60
                val retSeconds = seconds - 3600 * retHours - 60 * retMinutes
                val strHours = if (retHours < 10) "0$retHours" else retHours.toString()
                val strMinutes = if (retMinutes < 10) "0$retMinutes" else retMinutes.toString()
                val strSeconds = if (retSeconds < 10) "0$retSeconds" else retSeconds.toString()
                return if (retHours > 0) {
                    "$strHours:$strMinutes:$strSeconds"
                } else {
                    "$strMinutes:$strSeconds"
                }
            }
        }

        fun fromMillis(millis: Long): Millis {
            return Millis(millis)
        }

        fun fromSeconds(seconds: Long): Seconds {
            return Seconds(seconds)
        }
    }
}