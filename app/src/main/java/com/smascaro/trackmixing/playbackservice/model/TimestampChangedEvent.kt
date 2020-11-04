package com.smascaro.trackmixing.playbackservice.model

import com.smascaro.trackmixing.common.utils.time.Seconds

class TimestampChangedEvent(val newTimestamp: Seconds, val totalLength: Seconds)
