package com.smascaro.trackmixing.search.model.repository.datasource

import com.smascaro.trackmixing.base.time.TimeHelper
import timber.log.Timber
import kotlin.math.max

class ISO8601Converter {
    companion object {
        fun parseDuration(duration: String): Long {
            val millisIso8601 = parse(duration)
            return TimeHelper.fromMillis(millisIso8601).toSeconds()
        }

        private fun parse(duration: String): Long {
            if (!duration.startsWith("P")) {
                throw IllegalArgumentException("Duration in ISO8601 format must start with PT: $duration")
            }
            var derivedDuration = duration.substring(1)
            if (!derivedDuration.startsWith("T")) {
                throw IllegalArgumentException("Duration in ISO8601 format must start with PT: $derivedDuration")
            }
            derivedDuration = derivedDuration.substring(1)
            return parseDurationTime(derivedDuration)
        }

        private fun parseDurationTime(duration: String): Long {
            Timber.d("Parsing $duration")
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
                valueS =
                    duration.substring(max(indexH, indexM) + 1).takeWhile { it != 'S' }.toInt()
            }
            return ((valueS + 60 * valueM + 3600 * valueH) * 1000).toLong()
        }
    }
}