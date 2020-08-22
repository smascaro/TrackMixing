package com.smascaro.trackmixing.playbackservice

import android.app.ActivityManager
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.IBinder
import com.smascaro.trackmixing.TrackMixingApplication
import com.smascaro.trackmixing.common.data.model.ForegroundNotification
import com.smascaro.trackmixing.common.data.model.Track
import com.smascaro.trackmixing.common.utils.PLAYER_NOTIFICATION_ACTION_LOAD_TRACK
import com.smascaro.trackmixing.common.utils.PLAYER_NOTIFICATION_EXTRA_LOAD_TRACK_PARAM_KEY
import com.smascaro.trackmixing.playbackservice.controller.MixPlayerServiceController
import timber.log.Timber
import javax.inject.Inject

class MixPlayerService : BaseService(),
    MixPlayerServiceController.ServiceActionsDelegate {

    companion object {
        fun start(context: Context, track: Track): Boolean {
            val intent = Intent(context, MixPlayerService::class.java).apply {
                action =
                    PLAYER_NOTIFICATION_ACTION_LOAD_TRACK
            }
            val extras = Bundle().apply {
                putSerializable(PLAYER_NOTIFICATION_EXTRA_LOAD_TRACK_PARAM_KEY, track)
            }
            intent.putExtras(extras)
            val startedComponentName = context.startService(intent)
            return startedComponentName != null
        }

        fun ping(context: Context): Boolean {
            val activityManager =
                context.getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
            return activityManager.getRunningServices(Int.MAX_VALUE).any {
                it.service.className == MixPlayerService::class.java.name
            }
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
                PLAYER_NOTIFICATION_ACTION_LOAD_TRACK -> loadTrackFromIntent(intent, action)
                else -> controller.executeAction(action)
            }
        }

        return START_STICKY
    }

    private fun loadTrackFromIntent(intent: Intent, action: String?) {
        if (intent.extras != null &&
            intent.extras!!.containsKey(PLAYER_NOTIFICATION_EXTRA_LOAD_TRACK_PARAM_KEY)
        ) {
            val track = intent.extras!!.get(PLAYER_NOTIFICATION_EXTRA_LOAD_TRACK_PARAM_KEY) as Track
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