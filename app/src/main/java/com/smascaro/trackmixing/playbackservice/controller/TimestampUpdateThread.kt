package com.smascaro.trackmixing.playbackservice.controller

import com.smascaro.trackmixing.base.time.Milliseconds
import com.smascaro.trackmixing.base.time.TimeHelper
import com.smascaro.trackmixing.base.time.asMillis
import com.smascaro.trackmixing.playbackservice.model.TimestampChangedEvent
import com.smascaro.trackmixing.playbackservice.utils.BandPlaybackHelper
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.ensureActive
import kotlinx.coroutines.launch
import org.greenrobot.eventbus.EventBus
import timber.log.Timber

class TimestampUpdateThread(
    private val bandPlaybackHelper: BandPlaybackHelper,
    private val eventBus: EventBus,
    private val scope: CoroutineScope
) {
    private lateinit var job: Job
    private var currentTimestamp: Milliseconds = 0.asMillis()
    private val totalLength = bandPlaybackHelper.getTrack().secondsLong

    fun start() {
        job = run()
    }

    private fun run() = scope.launch {
        try {
            while (true) {
                ensureActive()
                currentTimestamp = bandPlaybackHelper.getTimestamp()
                Timber.d(
                    "Sending timestamp ${
                        TimeHelper.fromSeconds(currentTimestamp.seconds().value)
                            .toStringRepresentation()
                    }"
                )
                eventBus.post(
                    TimestampChangedEvent(
                        currentTimestamp.seconds(),
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