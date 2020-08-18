package com.smascaro.trackmixing.common

import android.app.Application
import com.smascaro.trackmixing.common.di.AppComponent
import com.smascaro.trackmixing.common.di.DaggerAppComponent
import timber.log.Timber

class TrackMixingApplication : Application() {

    override fun onCreate() {
        super.onCreate()
        Timber.plant(Timber.DebugTree())
    }

    val appComponent: AppComponent by lazy {
        DaggerAppComponent.factory().create(applicationContext)
    }
}