package com.smascaro.trackmixing.playbackservice.model

import com.smascaro.trackmixing.common.utils.PlaybackStateManager

sealed class PlaybackEvent {
    object StartServiceEvent : PlaybackEvent()
    object PlayMasterEvent : PlaybackEvent()
    object PauseMasterEvent : PlaybackEvent()
    class SetVolumeMasterEvent(val volume: Int) : PlaybackEvent()
    class SetVolumeTrackEvent(val trackInstrument: TrackInstrument, val volume: Int) :
        PlaybackEvent()

    class StateChanged(val newState: PlaybackStateManager.PlaybackState)
    class TimestampChanged(val newTimestamp: Int, val totalLength: Int)
    class SeekMaster(val seconds: Int)
    class StopMasterEvent
}
