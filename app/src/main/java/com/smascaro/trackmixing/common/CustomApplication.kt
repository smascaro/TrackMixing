package com.smascaro.trackmixing.common

import android.app.Application
import android.os.Build
import com.smascaro.trackmixing.common.di.CompositionRoot
import timber.log.Timber

class CustomApplication : Application() {
    private var mCompositionRoot = CompositionRoot()

    override fun onCreate() {
        super.onCreate()
        mCompositionRoot = CompositionRoot()

        Timber.plant(Timber.DebugTree())
    }

    fun getCompositionRoot(): CompositionRoot {
        return mCompositionRoot
    }
}