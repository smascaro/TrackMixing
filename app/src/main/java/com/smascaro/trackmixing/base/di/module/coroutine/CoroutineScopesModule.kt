package com.smascaro.trackmixing.base.di.module.coroutine

import com.smascaro.trackmixing.base.coroutine.IoCoroutineScope
import com.smascaro.trackmixing.base.coroutine.MainCoroutineScope
import dagger.Module
import dagger.Provides
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import javax.inject.Singleton

@Module
class CoroutineScopesModule {
    @Singleton
    @Provides
    fun provideMainCoroutineScope(): com.smascaro.trackmixing.base.coroutine.MainCoroutineScope {
        return object : com.smascaro.trackmixing.base.coroutine.MainCoroutineScope {
            override val coroutineContext = Job() + Dispatchers.Main
        }
    }

    @Singleton
    @Provides
    fun provideIoCoroutineScope(): com.smascaro.trackmixing.base.coroutine.IoCoroutineScope {
        return object : com.smascaro.trackmixing.base.coroutine.IoCoroutineScope {
            override val coroutineContext = Job() + Dispatchers.IO
        }
    }
}