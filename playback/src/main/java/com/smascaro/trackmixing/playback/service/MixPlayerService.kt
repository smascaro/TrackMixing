package com.smascaro.trackmixing.playback.service

import android.content.Context
import android.content.Intent
import android.os.Bundle
import com.smascaro.trackmixing.base.data.model.Track
import com.smascaro.trackmixing.base.service.BaseService
import com.smascaro.trackmixing.base.time.Seconds
import com.smascaro.trackmixing.playback.controller.MixPlayerServiceController
import com.smascaro.trackmixing.playback.controller.MixPlayerServiceController.ActionArgs
import com.smascaro.trackmixing.playback.di.component.PlaybackComponentProvider
import com.smascaro.trackmixing.playback.model.TrackInstrument
import timber.log.Timber
import javax.inject.Inject

class MixPlayerService : BaseService() {
    companion object {
        const val ACTION_PLAY_MASTER = "ACTION_PLAY_MASTER"
        const val ACTION_PAUSE_MASTER = "ACTION_PAUSE_MASTER"
        const val ACTION_LAUNCH_PLAYER = "ACTION_LAUNCH_PLAYER"
        const val ACTION_STOP_SERVICE = "ACTION_STOP_SERVICE"
        const val ACTION_LOAD_TRACK = "ACTION_LOAD_TRACK"
        const val EXTRA_LOAD_TRACK_PARAM_KEY = "EXTRA_LOAD_TRACK_PARAM_KEY"
        const val EXTRA_START_PLAYING_PARAM_KEY = "EXTRA_START_PLAYING_PARAM_KEY"
        const val ACTION_INSTRUMENT_VOLUME = "ACTION_INSTRUMENT_VOLUME"
        const val EXTRA_VOLUME_VALUE_PARAM_KEY = "EXTRA_VOLUME_VALUE_PARAM_KEY"
        const val EXTRA_VOLUME_INSTRUMENT_PARAM_KEY = "EXTRA_VOLUME_INSTRUMENT_PARAM_KEY"
        const val ACTION_SEEK = "ACTION_SEEK"
        const val EXTRA_SEEK_SECONDS_PARAM_KEY = "EXTRA_SEEK_SECONDS_PARAM_KEY"

        fun start(context: Context, track: Track, startPlaying: Boolean = true) =
            runCommand(context, ACTION_LOAD_TRACK, Bundle().apply {
                putSerializable(EXTRA_LOAD_TRACK_PARAM_KEY, track)
                putBoolean(EXTRA_START_PLAYING_PARAM_KEY, startPlaying)
            })

        fun play(context: Context) = runCommand(context, ACTION_PLAY_MASTER)

        fun pause(context: Context) = runCommand(context, ACTION_PAUSE_MASTER)

        fun stop(context: Context) = runCommand(context, ACTION_STOP_SERVICE)

        fun setVolume(context: Context, instrument: TrackInstrument, volume: Int) =
            runCommand(context, ACTION_INSTRUMENT_VOLUME, Bundle().apply {
                putSerializable(EXTRA_VOLUME_INSTRUMENT_PARAM_KEY, instrument)
                putInt(EXTRA_VOLUME_VALUE_PARAM_KEY, volume)
            })

        fun seek(context: Context, seconds: Seconds) =
            runCommand(
                context,
                ACTION_SEEK,
                Bundle().apply {
                    putInt(EXTRA_SEEK_SECONDS_PARAM_KEY, seconds.value.toInt())
                }
            )

        private fun runCommand(
            context: Context,
            commandAction: String,
            extras: Bundle = Bundle.EMPTY
        ): Boolean {
            val intent = Intent(context, MixPlayerService::class.java).apply {
                action = commandAction
                putExtras(extras)
            }
            return context.startService(intent) != null
        }
    }

    @Inject
    lateinit var controller: MixPlayerServiceController

    override fun onCreate() {
        (application as PlaybackComponentProvider).providePlaybackComponent().inject(this)
        super.onCreate()
        controller.onCreate()
        initializeControllerServiceCallbacks()
    }

    private fun initializeControllerServiceCallbacks() {
        controller.setStopServiceHandler {
            stopService(Intent(this, MixPlayerService::class.java))
        }
        controller.setStartForegroundHandler {
            startForeground(it.id, it.notification)
        }
        controller.setStopForegroundHandler {
            stopForeground(it)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        controller.onDestroy()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        if (intent != null) {
            Timber.d("Service running command: ${intent.action}")
            controller.executeAction(intent.action, ActionArgs(intent.extras))
        }
        return START_STICKY
    }
}