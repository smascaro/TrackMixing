package com.smascaro.trackmixing.playbackservice.model

import com.smascaro.trackmixing.common.data.model.Track
import com.smascaro.trackmixing.common.utils.PlaybackStateManager

sealed class PlaybackEvent {
    class StartServiceEvent : PlaybackEvent()
    class LoadTrackEvent(val track: Track) : PlaybackEvent()
    class PlayMasterEvent : PlaybackEvent()
    class PauseMasterEvent : PlaybackEvent()
    class SetVolumeMasterEvent(val volume: Int) : PlaybackEvent()
    class SetVolumeTrackEvent(val trackInstrument: TrackInstrument, val volume: Int) :
        PlaybackEvent()

    class StateChanged(val newState: PlaybackStateManager.PlaybackState)
    class TimestampChanged(val newTimestamp: Int, val totalLength: Int)
    class SeekMaster(val seconds: Int)
    class StopMasterEvent
}
