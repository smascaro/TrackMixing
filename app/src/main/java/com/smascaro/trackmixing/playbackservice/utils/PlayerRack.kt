package com.smascaro.trackmixing.playbackservice.utils

import com.smascaro.trackmixing.common.utils.TrackVolumeBundle
import com.smascaro.trackmixing.playbackservice.model.TrackInstrument

class PlayerRack : PlayerActions {
    private val rack: HashMap<TrackInstrument, PlayingTrackState> = hashMapOf()

    fun put(instrument: TrackInstrument, playingTrackState: PlayingTrackState) {
        rack[instrument] = playingTrackState
    }

    override fun play() {
        rack.forEach { it.value.play() }
    }

    override fun play(instrument: TrackInstrument) {
        rack[instrument]?.play()
    }

    override fun pause() {
        rack.forEach { it.value.pause() }
    }

    override fun pause(instrument: TrackInstrument) {
        rack[instrument]?.pause()
    }

    override fun seek(newPosition: Int) {
        rack.forEach { it.value.seek(newPosition) }
    }

    override fun getVolume(instrument: TrackInstrument): Float {
        return rack[instrument]?.getVolume() ?: 0f
    }

    override fun setVolume(volume: Int, instrument: TrackInstrument?) {
        if (instrument == null) {
            setVolumeMaster(volume)
        } else {
            setVolumeInstrument(instrument, volume)
        }
    }

    private fun setVolumeInstrument(instrument: TrackInstrument, volume: Int) {
        rack[instrument]?.setVolume(volume)
    }

    private fun setVolumeMaster(volume: Int) {
        rack.forEach { it.value.setVolume(volume) }
    }

    override fun getCurrentPosition(): Long {
        return rack.values.maxOf { it.getTimestampSeconds() }
    }

    override fun isCompleted(instrument: TrackInstrument): Boolean {
        return rack[instrument]?.isCompleted() ?: true
    }

    fun allCompleted(): Boolean {
        return rack.values.all { it.isCompleted() }
    }

    fun clear() {
        rack.clear()
    }

    fun unregisterListener(listener: PlayingTrackState.Listener) {
        rack.forEach { it.value.unregisterListener(listener) }
    }

    fun finalize() {
        rack.forEach { it.value.finalize() }
    }

    fun getVolumesBundle(): TrackVolumeBundle {
        return TrackVolumeBundle(
            getVolume(TrackInstrument.VOCALS).toInt(),
            getVolume(TrackInstrument.OTHER).toInt(),
            getVolume(TrackInstrument.BASS).toInt(),
            getVolume(TrackInstrument.DRUMS).toInt()
        )
    }
}