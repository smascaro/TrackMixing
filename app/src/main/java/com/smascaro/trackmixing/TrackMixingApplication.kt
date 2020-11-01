package com.smascaro.trackmixing

import android.app.Application
import com.smascaro.trackmixing.common.di.AppComponent
import com.smascaro.trackmixing.common.di.DaggerAppComponent
import timber.log.Timber

class TrackMixingApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        if (BuildConfig.DEBUG) {
            Timber.plant(Timber.DebugTree())
        }
    }

    val appComponent: AppComponent by lazy {
        DaggerAppComponent.factory().create(applicationContext)
    }
}