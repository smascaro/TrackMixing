package com.smascaro.trackmixing.common.di.coroutines

import dagger.Module
import dagger.Provides
import kotlinx.coroutines.CoroutineScope
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