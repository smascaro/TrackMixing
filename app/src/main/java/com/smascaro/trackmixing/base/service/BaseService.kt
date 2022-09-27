package com.smascaro.trackmixing.base.service

import android.content.Intent
import android.os.IBinder
import dagger.android.DaggerService

open class BaseService : DaggerService() {
    override fun onBind(intent: Intent?): IBinder? {
        return null
    }
}