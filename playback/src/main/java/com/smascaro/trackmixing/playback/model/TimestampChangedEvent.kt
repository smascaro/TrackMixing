package com.smascaro.trackmixing.playback.model

import com.smascaro.trackmixing.base.time.Seconds

class TimestampChangedEvent(val newTimestamp: Seconds, val totalLength: Seconds)
