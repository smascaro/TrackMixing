package com.smascaro.trackmixing.service.events

import com.smascaro.trackmixing.tracks.Track

sealed class PlaybackEvent {
    class StartServiceEvent : PlaybackEvent()
    class LoadTrackEvent(val track: Track) : PlaybackEvent()
    class PlayMasterEvent : PlaybackEvent()
    class PauseMasterEvent : PlaybackEvent()
}
