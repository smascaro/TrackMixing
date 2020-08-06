package com.smascaro.trackmixing.service.common

import android.app.Service
import android.content.Intent
import android.os.IBinder
import com.smascaro.trackmixing.common.TrackMixingApplication
import com.smascaro.trackmixing.service.di.ServiceControllerCompositionRoot

open class BaseService : Service() {
    private var mControllerCompositionRoot: ServiceControllerCompositionRoot? = null

    protected fun getCompositionRoot(): ServiceControllerCompositionRoot {
        if (mControllerCompositionRoot == null) {
            return ServiceControllerCompositionRoot(
                (application as TrackMixingApplication).getCompositionRoot(),
                this
            )
        }
        return mControllerCompositionRoot!!
    }


    override fun onBind(intent: Intent?): IBinder? {
        return null
    }
}