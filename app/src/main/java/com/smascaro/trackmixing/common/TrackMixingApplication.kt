package com.smascaro.trackmixing.common

import android.app.Application
import com.smascaro.trackmixing.common.di.AppComponent
import com.smascaro.trackmixing.common.di.CompositionRoot
import com.smascaro.trackmixing.common.di.DaggerAppComponent
import timber.log.Timber

class TrackMixingApplication : Application() {
    private var mCompositionRoot = CompositionRoot()

    override fun onCreate() {
        super.onCreate()
        mCompositionRoot = CompositionRoot()

        Timber.plant(Timber.DebugTree())
    }

    val appComponent: AppComponent by lazy {
        DaggerAppComponent.factory().create(applicationContext)
    }


    fun getCompositionRoot(): CompositionRoot {
        return mCompositionRoot
    }
}