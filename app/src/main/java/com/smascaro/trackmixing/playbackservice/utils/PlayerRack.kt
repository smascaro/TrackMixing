package com.smascaro.trackmixing.playbackservice.utils

import com.smascaro.trackmixing.common.utils.TrackVolumeBundle
import com.smascaro.trackmixing.playbackservice.model.TrackInstrument
import kotlinx.coroutines.*
import kotlin.system.measureTimeMillis

class PlayerRack : PlayerActions {
    private val rack: HashMap<TrackInstrument, PlayingTrackState> = hashMapOf()

    fun put(instrument: TrackInstrument, playingTrackState: PlayingTrackState) {
        rack[instrument] = playingTrackState
    }

    override fun play() {
        CoroutineScope(Dispatchers.Main).launch {
            rack.map { async { it.value.play() } }.awaitAll()
        }
//        rack.forEach { it.value.play() }
    }

    override fun play(instrument: TrackInstrument) {
        rack[instrument]?.play()
    }

    override fun pause() = rack.forEach { it.value.pause() }

    override fun pause(instrument: TrackInstrument) {
        rack[instrument]?.pause()
    }

    override fun seek(newPosition: Int) = rack.forEach { it.value.seek(newPosition) }
    override fun seekMillis(newPositionMillis: Long) =
        rack.forEach { it.value.seekMillis(newPositionMillis) }

    override fun getVolume(instrument: TrackInstrument): Float = rack[instrument]?.getVolume() ?: 0f

    override fun setVolume(volume: Int, instrument: TrackInstrument?) {
        if (instrument == null) {
            setVolumeMaster(volume)
        } else {
            setVolumeInstrument(instrument, volume)
        }
    }

    private fun setVolumeInstrument(
        instrument: TrackInstrument,
        volume: Int
    ) = rack[instrument]?.setVolume(volume)

    private fun setVolumeMaster(volume: Int) = rack.forEach { it.value.setVolume(volume) }

    override fun getCurrentPosition(): Long = rack.values.maxOf { it.getTimestampSeconds() }

    override fun isCompleted(instrument: TrackInstrument): Boolean =
        rack[instrument]?.isCompleted() ?: true

    override fun isReadyToPlay(): Boolean = rack.values.all { it.readyToPlay }

    fun allCompleted(): Boolean = rack.values.all { it.isCompleted() }

    fun clear() = rack.clear()

    fun unregisterListener(listener: PlayingTrackState.Listener) =
        rack.forEach { it.value.unregisterListener(listener) }

    fun finalize() = rack.forEach { it.value.finalize() }

    fun getVolumesBundle(): TrackVolumeBundle {
        return TrackVolumeBundle(
            getVolume(TrackInstrument.VOCALS).toInt(),
            getVolume(TrackInstrument.OTHER).toInt(),
            getVolume(TrackInstrument.BASS).toInt(),
            getVolume(TrackInstrument.DRUMS).toInt()
        )
    }

    suspend fun getCurrentPositionsReport(cb: (report: List<Pair<TrackInstrument, Long>>) -> Unit) =
        CoroutineScope(Dispatchers.Main).launch {
            var report = listOf<Pair<TrackInstrument, Long>>()
            val millisGetPositions = measureTimeMillis {
                val asyncJobVocals =
                    async { TrackInstrument.VOCALS to rack[TrackInstrument.VOCALS]!!.getCurrentPosition() }
                val asyncJobOther =
                    async { TrackInstrument.OTHER to rack[TrackInstrument.OTHER]!!.getCurrentPosition() }
                val asyncJobBass =
                    async { TrackInstrument.BASS to rack[TrackInstrument.BASS]!!.getCurrentPosition() }
                val asyncJobDrums =
                    async { TrackInstrument.DRUMS to rack[TrackInstrument.DRUMS]!!.getCurrentPosition() }
                report = awaitAll(asyncJobVocals, asyncJobBass, asyncJobOther, asyncJobDrums)
            }
            cb(report)
        }
}