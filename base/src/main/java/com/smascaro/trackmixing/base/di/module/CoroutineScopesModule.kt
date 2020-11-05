package com.smascaro.trackmixing.base.di.module

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
    fun provideMainCoroutineScope(): MainCoroutineScope {
        return object : MainCoroutineScope {
            override val coroutineContext = Job() + Dispatchers.Main
        }
    }

    @Singleton
    @Provides
    fun provideIoCoroutineScope(): IoCoroutineScope {
        return object : IoCoroutineScope {
            override val coroutineContext = Job() + Dispatchers.IO
        }
    }
}