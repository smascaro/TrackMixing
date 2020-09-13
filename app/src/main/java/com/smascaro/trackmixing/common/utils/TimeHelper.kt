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

        class StringRepresentation(val time: String) {
            fun toSeconds(): Int {
                if (!time.contains(':')) {
                    throw Exception("Supplied time is not valid: $time")
                }
                val components = time.split(':')
                if (components.size > 3) {
                    throw Exception("Supplied time is not valid: $time")
                }
                var seconds = 0
                var minutes = 0
                var hours = 0
                when (components.size) {
                    2 -> {
                        minutes = components[0].toInt()
                        seconds = components[1].toInt()
                    }
                    3 -> {
                        hours = components[0].toInt()
                        minutes = components[1].toInt()
                        seconds = components[2].toInt()
                    }
                }
//                components.forEachIndexed { index, s ->
//                    when (components.size) {
//                        2 -> when (index) {
//                            0 -> minutes = s.toInt()
//                            1 -> seconds = s.toInt()
//                        }
//                        3 -> when (index) {
//                            0 -> hours = s.toInt()
//                            1 -> minutes = s.toInt()
//                            2 -> seconds = s.toInt()
//                        }
//                    }
//                }
                return (seconds + (60 * minutes) + (60 * 60 * hours))
            }
        }

        fun fromMillis(millis: Long): Millis {
            return Millis(millis)
        }

        fun fromSeconds(seconds: Long): Seconds {
            return Seconds(seconds)
        }

        fun fromString(time: String): StringRepresentation {
            return StringRepresentation(time)
        }
    }
}