package com.smascaro.trackmixing.ui.player

import android.app.PendingIntent
import android.content.Intent
import android.os.Bundle
import com.smascaro.trackmixing.common.NOTIFICATION_ACTION_LOAD_TRACK
import com.smascaro.trackmixing.common.NOTIFICATION_ACTION_PAUSE_MASTER
import com.smascaro.trackmixing.common.NOTIFICATION_ACTION_PLAY_MASTER
import com.smascaro.trackmixing.common.NOTIFICATION_EXTRA_LOAD_TRACK_PARAM_KEY
import com.smascaro.trackmixing.service.MixPlayerService
import com.smascaro.trackmixing.service.events.PlayMasterEvent
import com.smascaro.trackmixing.service.events.StartServiceEvent
import com.smascaro.trackmixing.tracks.Track
import com.smascaro.trackmixing.ui.common.BaseObservableViewMvc
import org.greenrobot.eventbus.EventBus
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
        if (getContext() != null) {
            mIsServiceStarted = MixPlayerService.start(getContext()!!)
            EventBus.getDefault().post(StartServiceEvent())
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
        EventBus.getDefault().post(PlayMasterEvent())
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