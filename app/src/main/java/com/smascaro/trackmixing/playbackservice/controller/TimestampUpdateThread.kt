package com.smascaro.trackmixing.playbackservice.controller

import com.smascaro.trackmixing.common.utils.TimeHelper
import com.smascaro.trackmixing.playbackservice.model.TimestampChangedEvent
import com.smascaro.trackmixing.playbackservice.utils.BandPlaybackHelper
import kotlinx.coroutines.*
import org.greenrobot.eventbus.EventBus
import timber.log.Timber

class TimestampUpdateThread(
    private val bandPlaybackHelper: BandPlaybackHelper,
    private val eventBus: EventBus,
    private val scope: CoroutineScope
) {
    private lateinit var job: Job
    private var currentTimestampSeconds: Int = 0
    private val totalLength = bandPlaybackHelper.getTrack().secondsLong

    fun start() {
        job = run()
    }

    private fun run() = scope.launch {
        try {
            while (true) {
                ensureActive()
                currentTimestampSeconds = bandPlaybackHelper.getTimestampSeconds()
                Timber.d(
                    "Sending timestamp ${
                        TimeHelper.fromSeconds(currentTimestampSeconds.toLong())
                            .toStringRepresentation()
                    }"
                )
                eventBus.post(
                    TimestampChangedEvent(
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
        Timber.d("Job got cancelled successfully!")
    }

    fun cancel() {
        job.cancel()
    }
}