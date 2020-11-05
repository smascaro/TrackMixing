package com.smascaro.trackmixing.base

import android.app.Application
import timber.log.Timber

class BaseApplication : Application() {
    companion object {
        // val baseComponent by lazy {
        //     DaggerBaseComponent.builder().build()
        // }
        // @JvmStatic
        // lateinit var baseComponent: BaseComponent
    }

    override fun onCreate() {
        super.onCreate()
        if (BuildConfig.DEBUG) {
            Timber.plant(Timber.DebugTree())
        }
        // baseComponent = DaggerBaseComponent.builder().withContext(applicationContext).build()
    }
}