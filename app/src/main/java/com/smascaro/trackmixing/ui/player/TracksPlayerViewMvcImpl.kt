package com.smascaro.trackmixing.ui.player

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.Build
import android.os.IBinder
import android.view.LayoutInflater
import android.view.ViewGroup
import com.smascaro.trackmixing.R
import com.smascaro.trackmixing.service.MixPlayerService
import com.smascaro.trackmixing.tracks.Track
import com.smascaro.trackmixing.ui.common.BaseObservableViewMvc
import timber.log.Timber

class TracksPlayerViewMvcImpl(
    layoutInflater: LayoutInflater,
    parent: ViewGroup?
) :
    BaseObservableViewMvc<TracksPlayerViewMvc.Listener>(),
    TracksPlayerViewMvc {
    private lateinit var mMixPlayerServiceBinder: MixPlayerService.Binder
    private var mServiceIntent: Intent? = null
    private var mIsServiceConnected: Boolean = false
    private val mPlayerServiceConnection = object : ServiceConnection {
        override fun onServiceDisconnected(name: ComponentName?) {
            mIsServiceConnected = false
            Timber.d("Service $name disconnected")
        }

        override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
            val binder = service as MixPlayerService.Binder
            Timber.d("Service bound successfully")
            mMixPlayerServiceBinder = binder
            mIsServiceConnected = true
            getListeners().forEach {
                it.onServiceConnected()
            }
        }

    }

    init {
        setRootView(layoutInflater.inflate(R.layout.activity_track_player, parent, false))
    }

    override fun isServiceConnected(): Boolean {
        return mIsServiceConnected
    }

    override fun startService() {
        if (mServiceIntent == null) {
            mServiceIntent = Intent(getContext(), MixPlayerService::class.java)
            Timber.d("Binding to service")
            getContext()?.bindService(
                mServiceIntent,
                mPlayerServiceConnection,
                Context.BIND_AUTO_CREATE
            )
//            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
//                getContext()?.startForegroundService(mServiceIntent)
//            } else {
//                getContext()?.startService(mServiceIntent)
//            }
        }
    }

    override fun onDestroy() {
        getContext()?.unbindService(mPlayerServiceConnection)
    }

    override fun loadTrack(track: Track) {
        mMixPlayerServiceBinder.loadTrack(track)
    }

    override fun playMaster() {
        mMixPlayerServiceBinder.play()
    }

    override fun pauseMaster() {
        mMixPlayerServiceBinder.pause()
    }
}