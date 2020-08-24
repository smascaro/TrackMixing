package com.smascaro.trackmixing.playbackservice.utils

import android.content.Context
import com.smascaro.trackmixing.common.data.model.Track
import com.smascaro.trackmixing.common.utils.PlaybackStateManager
import com.smascaro.trackmixing.playbackservice.MixPlayerService
import com.smascaro.trackmixing.playbackservice.model.PlaybackEvent
import com.smascaro.trackmixing.playbackservice.model.TrackInstrument
import org.greenrobot.eventbus.EventBus
import javax.inject.Inject

class PlaybackSessionImpl @Inject constructor(
    private val context: Context,
    private val playbackStateManager: PlaybackStateManager
) :
    PlaybackSession {

    private val eventBus = EventBus.getDefault()
    private var isServiceStarted: Boolean = false

    override fun isSessionInitialized() = isServiceStarted


    override fun startPlayback(track: Track): Boolean {
        isServiceStarted =
            MixPlayerService.start(
                context,
                track
            )
        initialize(track)
        return isSessionInitialized()
    }

    private fun initialize(track: Track) {
        //No actions yet
    }

    override fun stopPlayback() {
//        TODO("Not yet implemented")
    }

    override fun play() {
        eventBus.post(PlaybackEvent.PlayMasterEvent())
    }

    override fun pause() {
        eventBus.post(PlaybackEvent.PauseMasterEvent())
    }

    override fun seek(seconds: Int) {
//        TODO("Not yet implemented")
    }

    override fun setMasterVolume(volume: Int) {
        eventBus.post(PlaybackEvent.SetVolumeMasterEvent(volume))
    }

    override fun setTrackVolume(trackInstrument: TrackInstrument, volume: Int) {
        eventBus.post(PlaybackEvent.SetVolumeTrackEvent(trackInstrument, volume))
    }

    override fun getState(): PlaybackStateManager.PlaybackState {
        return playbackStateManager.getPlayingState()
    }

}