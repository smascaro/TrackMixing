package com.smascaro.trackmixing.playbackservice.controller

import com.smascaro.trackmixing.common.utils.TimeHelper
import com.smascaro.trackmixing.playbackservice.model.PlaybackEvent
import com.smascaro.trackmixing.playbackservice.utils.PlaybackHelper
import kotlinx.coroutines.*
import org.greenrobot.eventbus.EventBus
import timber.log.Timber

class TimestampUpdateThread(
    private val playbackHelper: PlaybackHelper,
    private val eventBus: EventBus
) {
    private lateinit var job: Job
    private var currentTimestampSeconds: Int = 0
    private val totalLength = playbackHelper.getTrack().secondsLong
    fun start() {
        job = run()
    }

    private fun run() = CoroutineScope(Dispatchers.Main).launch {
        try {
            while (true) {
                ensureActive()
                currentTimestampSeconds = playbackHelper.getTimestampSeconds()
                Timber.d(
                    "Sending timestamp ${
                        TimeHelper.fromSeconds(currentTimestampSeconds.toLong())
                            .toStringRepresentation()
                    }"
                )
                eventBus.post(
                    PlaybackEvent.TimestampChanged(
                        currentTimestampSeconds,
                        totalLength
                    )
                )
                delay(1 * 1000)
            }
        } catch (e: CancellationException) {
            Timber.e(e)
        } catch (e: Exception) {
            Timber.e(e)
            Timber.w("Stopping thread ${Thread.currentThread().name} due to exception")
        }
        Timber.d("Job got cancelled succesfully!")
    }

    fun cancel() {
        job.cancel()
    }
}