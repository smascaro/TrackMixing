package com.smascaro.trackmixing.service.common

import android.app.Service
import android.content.Intent
import android.os.IBinder
import androidx.navigation.NavController
import androidx.navigation.findNavController
import com.smascaro.trackmixing.R
import com.smascaro.trackmixing.common.CustomApplication
import com.smascaro.trackmixing.common.di.ControllerCompositionRoot
import com.smascaro.trackmixing.service.di.ServiceControllerCompositionRoot

open class BaseService : Service() {
    private var mControllerCompositionRoot: ServiceControllerCompositionRoot? = null

    protected fun getCompositionRoot(): ServiceControllerCompositionRoot {
        if (mControllerCompositionRoot == null) {
            return ServiceControllerCompositionRoot(
                (application as CustomApplication).getCompositionRoot(),
                this
            )
        }
        return mControllerCompositionRoot!!
    }


    override fun onBind(intent: Intent?): IBinder? {
        return null
    }
}