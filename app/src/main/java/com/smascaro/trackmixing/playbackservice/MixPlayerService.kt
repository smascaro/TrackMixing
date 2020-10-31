package com.smascaro.trackmixing.playbackservice

import android.content.Context
import android.content.Intent
import android.os.Bundle
import com.smascaro.trackmixing.TrackMixingApplication
import com.smascaro.trackmixing.common.data.model.ForegroundNotification
import com.smascaro.trackmixing.common.data.model.Track
import com.smascaro.trackmixing.playbackservice.controller.MixPlayerServiceController
import com.smascaro.trackmixing.playbackservice.controller.MixPlayerServiceController.ActionArgs
import com.smascaro.trackmixing.playbackservice.utils.PlayerNotificationHelper
import javax.inject.Inject

class MixPlayerService : BaseService(),
    MixPlayerServiceController.ServiceActionsDelegate {
    companion object {
        fun start(context: Context, track: Track, startPlaying: Boolean = true): Boolean {
            val intent = Intent(context, MixPlayerService::class.java).apply {
                action = PlayerNotificationHelper.ACTION_LOAD_TRACK
            }
            val extras = Bundle().apply {
                putSerializable(PlayerNotificationHelper.EXTRA_LOAD_TRACK_PARAM_KEY, track)
                putBoolean(PlayerNotificationHelper.EXTRA_START_PLAYING_PARAM_KEY, startPlaying)
            }

            intent.putExtras(extras)
            val startedComponentName = context.startService(intent)
            return startedComponentName != null
        }
    }

    @Inject
    lateinit var controller: MixPlayerServiceController

    override fun onCreate() {
        (application as TrackMixingApplication).appComponent.playerComponent().create().inject(this)
        super.onCreate()
        controller.onCreate()
        controller.registerListener(this)
    }

    override fun onDestroy() {
        super.onDestroy()
        controller.onDestroy()
        controller.unregisterListener(this)
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        if (intent != null) {
            controller.executeAction(intent.action, ActionArgs(intent.extras))
        }
        return START_STICKY
    }

    override fun onStopService() {
        stopService(Intent(this, MixPlayerService::class.java))
    }

    override fun onStartForeground(notification: ForegroundNotification) {
        startForeground(notification.id, notification.notification)
    }

    override fun onStopForeground(removeNotification: Boolean) {
        stopForeground(removeNotification)
    }
}