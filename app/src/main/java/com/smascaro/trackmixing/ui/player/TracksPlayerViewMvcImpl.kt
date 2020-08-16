package com.smascaro.trackmixing.ui.player

import android.app.PendingIntent
import android.content.Intent
import android.os.Bundle
import com.smascaro.trackmixing.common.*
import com.smascaro.trackmixing.service.MixPlayerService
import com.smascaro.trackmixing.tracks.Track
import com.smascaro.trackmixing.ui.common.BaseObservableViewMvc
import javax.inject.Inject

class TracksPlayerViewMvcImpl @Inject constructor() :
    BaseObservableViewMvc<TracksPlayerViewMvc.Listener>(),
    TracksPlayerViewMvc {

    private var mServiceIntent: Intent? = null
    private var mIsServiceStarted: Boolean = false

    override fun isServiceStarted(): Boolean {
        return mIsServiceStarted
    }

    override fun startService() {
        if (mServiceIntent == null) {
            mServiceIntent = Intent(getContext(), MixPlayerService::class.java).apply {
                action = NOTIFICATION_ACTION_START_SERVICE
            }
            val startedComponentName = getContext()?.startService(mServiceIntent)
            mIsServiceStarted = startedComponentName != null
        }
    }

    override fun onDestroy() {
        //no action
    }

    override fun loadTrack(track: Track) {
        val extras = Bundle().apply {
            putSerializable(NOTIFICATION_EXTRA_LOAD_TRACK_PARAM_KEY, track)
        }
        sendActionToService(NOTIFICATION_ACTION_LOAD_TRACK, extras)
    }

    override fun playMaster() {
        sendActionToService(NOTIFICATION_ACTION_PLAY_MASTER)
    }

    override fun pauseMaster() {
        sendActionToService(NOTIFICATION_ACTION_PAUSE_MASTER)
    }

    private fun sendActionToService(action: String, extras: Bundle? = null) {
        val intent = Intent(getContext(), MixPlayerService::class.java)
        intent.action = action
        if (extras != null) {
            intent.putExtras(extras)
        }
        val pendingIntent =
            PendingIntent.getService(getContext(), 2, intent, PendingIntent.FLAG_ONE_SHOT)
        pendingIntent.send()
    }


}