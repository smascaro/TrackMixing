package com.smascaro.trackmixing.base.time

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

        private const val THRESHOLD_SECOND = 1000L
        private const val THRESHOLD_MINUTE = 60 * THRESHOLD_SECOND
        private const val THRESHOLD_HOUR = THRESHOLD_MINUTE * 60
        private const val THRESHOLD_DAY = THRESHOLD_HOUR * 24
        private const val THRESHOLD_WEEK = THRESHOLD_DAY * 7
        private const val THRESHOLD_MONTH = THRESHOLD_DAY * 30
        private const val THRESHOLD_YEAR = THRESHOLD_DAY * 365
        fun elapsedTime(millis: Long): String {
            val elapsedMillis = System.currentTimeMillis() - millis
            return when {
                elapsedMillis >= THRESHOLD_YEAR -> "${(elapsedMillis / THRESHOLD_YEAR).toInt()}y ago"
                elapsedMillis >= THRESHOLD_MONTH -> "${(elapsedMillis / THRESHOLD_MONTH).toInt()}mo ago"
                elapsedMillis >= THRESHOLD_WEEK -> "${(elapsedMillis / THRESHOLD_WEEK).toInt()}w ago"
                elapsedMillis >= THRESHOLD_DAY -> "${(elapsedMillis / THRESHOLD_DAY).toInt()}d ago"
                elapsedMillis >= THRESHOLD_HOUR -> "${(elapsedMillis / THRESHOLD_HOUR).toInt()}h ago"
                elapsedMillis >= THRESHOLD_MINUTE -> "${(elapsedMillis / THRESHOLD_MINUTE).toInt()}m ago"
                else -> "${(elapsedMillis / THRESHOLD_SECOND).toInt()}s ago"
            }
        }
    }
}