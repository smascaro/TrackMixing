package com.smascaro.trackmixing.search.model.repository.datasource

import com.smascaro.trackmixing.common.utils.TimeHelper

class ISO8601Converter {
    companion object {
        fun parseDuration(duration: String): Long {
            val millisIso8601 = parse(duration)
            return TimeHelper.fromMillis(millisIso8601).toSeconds()
        }

        private fun parse(duration: String): Long {
            if (!duration.startsWith("P")) {
                throw IllegalArgumentException("Duration in ISO8601 format must start with PT")
            }
            var derivedDuration = duration.substring(1)
            if (!derivedDuration.startsWith("T")) {
                throw IllegalArgumentException("Duration in ISO8601 format must start with PT")
            }
            derivedDuration = derivedDuration.substring(1)
            return parseDurationTime(derivedDuration)
        }

        private fun parseDurationTime(duration: String): Long {
            val indexH = duration.indexOfFirst { it == 'H' }
            val indexM = duration.indexOfFirst { it == 'M' }
            val indexS = duration.indexOfFirst { it == 'S' }
            var valueH = 0
            var valueM = 0
            var valueS = 0
            if (indexH >= 0) {
                valueH = duration.takeWhile { it != 'H' }.toInt()
            }
            if (indexM >= 0) {
                valueM = if (indexH >= 0) {
                    duration.substring(indexH + 1).takeWhile { it != 'M' }.toInt()
                } else {
                    duration.takeWhile { it != 'M' }.toInt()
                }
            }
            if (indexS >= 0) {
                valueS = if (indexM >= 0) {
                    duration.substring(indexM + 1).takeWhile { it != 'S' }.toInt()
                } else {
                    duration.takeWhile { it != 'S' }.toInt()
                }
            }
            return ((valueS + 60 * valueM + 3600 * valueH) * 1000).toLong()
        }

        class Result(val value: String)
    }
}