package com.smascaro.trackmixing.playbackservice.utils

import com.smascaro.trackmixing.base.coroutine.MainCoroutineScope
import com.smascaro.trackmixing.base.time.Milliseconds
import com.smascaro.trackmixing.base.time.Seconds
import com.smascaro.trackmixing.base.time.asMillis
import com.smascaro.trackmixing.common.utils.TrackVolumeBundle
import com.smascaro.trackmixing.playbackservice.model.TrackInstrument
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.launch
import kotlin.system.measureTimeMillis

class PlayerRack(private val ui: MainCoroutineScope) : PlayerActions {
    private val rack: HashMap<TrackInstrument, InstrumentPlayer> = hashMapOf()

    fun put(instrument: TrackInstrument, instrumentPlayer: InstrumentPlayer) {
        rack[instrument] = instrumentPlayer
    }

    override fun play() {
        ui.launch {
            rack.map { async { it.value.play() } }.awaitAll()
        }
    }

    override fun play(instrument: TrackInstrument) {
        rack[instrument]?.play()
    }

    override fun pause() = rack.forEach { it.value.pause() }

    override fun pause(instrument: TrackInstrument) {
        rack[instrument]?.pause()
    }

    override fun seek(newPosition: Seconds) = rack.forEach { it.value.seek(newPosition) }
    override fun seekMillis(newPositionMillis: Milliseconds) =
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

    override fun getCurrentPosition(): Milliseconds = rack.values.maxOf { it.getTimestamp().value }.asMillis()

    override fun isCompleted(instrument: TrackInstrument): Boolean =
        rack[instrument]?.isCompleted() ?: true

    override fun isReadyToPlay(): Boolean = rack.values.all { it.readyToPlay }

    fun allCompleted(): Boolean = rack.values.all { it.isCompleted() }

    fun clear() = rack.clear()

    fun unregisterListener(listener: InstrumentPlayer.Listener) =
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

    suspend fun getCurrentPositionsReport(cb: (report: List<Pair<TrackInstrument, Milliseconds>>) -> Unit) =
        ui.launch {
            var report = listOf<Pair<TrackInstrument, Milliseconds>>()
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