package com.smascaro.trackmixing.service

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.IBinder
import com.smascaro.trackmixing.common.NOTIFICATION_ACTION_LOAD_TRACK
import com.smascaro.trackmixing.common.NOTIFICATION_EXTRA_LOAD_TRACK_PARAM_KEY
import com.smascaro.trackmixing.common.TrackMixingApplication
import com.smascaro.trackmixing.service.common.BaseService
import com.smascaro.trackmixing.tracks.Track
import timber.log.Timber
import javax.inject.Inject

class MixPlayerService : BaseService(),
    MixPlayerServiceController.ServiceActionsDelegate {

    companion object {
        fun start(context: Context, track: Track): Boolean {
            val intent = Intent(context, MixPlayerService::class.java).apply {
                action = NOTIFICATION_ACTION_LOAD_TRACK
            }
            val extras = Bundle().apply {
                putSerializable(NOTIFICATION_EXTRA_LOAD_TRACK_PARAM_KEY, track)
            }
            intent.putExtras(extras)
            val startedComponentName = context.startService(intent)
            return startedComponentName != null
        }
    }

    @Inject
    lateinit var controller: MixPlayerServiceController

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

    override fun onCreate() {
        (application as TrackMixingApplication).appComponent.playerComponent().create().inject(this)
        super.onCreate()
        controller.onCreate()
        controller.registerListener(this)
    }

    fun stopService() {
        controller.stopService()
    }

    override fun onDestroy() {
        super.onDestroy()
        controller.onDestroy()
        controller.unregisterListener(this)
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        if (intent != null) {
            val action = intent.action
            when (action) {
                NOTIFICATION_ACTION_LOAD_TRACK -> loadTrackFromIntent(intent, action)
                else -> controller.executeAction(action)
            }
        }

        return START_STICKY
    }

    private fun loadTrackFromIntent(intent: Intent, action: String?) {
        if (intent.extras != null &&
            intent.extras!!.containsKey(NOTIFICATION_EXTRA_LOAD_TRACK_PARAM_KEY)
        ) {
            val track = intent.extras!!.get(NOTIFICATION_EXTRA_LOAD_TRACK_PARAM_KEY) as Track
            loadTrack(track)
        } else {
            Timber.w("$action action called but no track parameter supplied")
        }
    }

    private fun loadTrack(track: Track) {
        controller.loadTrack(track)
    }

    private fun playMaster() {
        controller.playMaster()
    }

    private fun pauseMaster() {
        controller.pauseMaster()
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