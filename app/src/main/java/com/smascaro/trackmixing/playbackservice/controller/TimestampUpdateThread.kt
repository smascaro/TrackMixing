package com.smascaro.trackmixing.playbackservice.controller

import com.smascaro.trackmixing.common.utils.TimeHelper
import com.smascaro.trackmixing.playbackservice.model.PlaybackEvent
import com.smascaro.trackmixing.playbackservice.utils.PlaybackHelper
import kotlinx.coroutines.*
import org.greenrobot.eventbus.EventBus
import timber.log.Timber

class TimestampUpdateThread(private val playbackHelper: PlaybackHelper) {
    private lateinit var job: Job
    private var currentTimestampSeconds: Int = 0
    fun start() {
        job = run()

    }

    private fun run() = CoroutineScope(Dispatchers.IO).launch {
        try {
            while (true) {
                ensureActive()
                currentTimestampSeconds = playbackHelper.getTimestampMillis() / 1000
                Timber.d(
                    "Sending timestamp ${TimeHelper.fromSeconds(currentTimestampSeconds.toLong())
                        .toStringRepresentation()}"
                )
                EventBus.getDefault().post(PlaybackEvent.TimestampChanged(currentTimestampSeconds))
                delay(1 * 1000)
            }
        } catch (e: CancellationException) {
            Timber.e(e)
        }

        Timber.d("Job got cancelled succesfully!")
    }

    fun cancel() {
        job.cancel()
    }
}