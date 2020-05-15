package com.smascaro.trackmixing.data

import com.smascaro.trackmixing.networking.availableTracks.AvailableTracksResponseSchema
import com.smascaro.trackmixing.tracks.Track

fun AvailableTracksResponseSchema.Item.toModel(): Track {
    return Track(title, videoId, thumbnailUrl ?: "", secondsLong)
}