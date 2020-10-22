package com.smascaro.trackmixing.common.di.coroutines

import dagger.Module
import dagger.Provides
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import javax.inject.Singleton

@Module
class CoroutineScopesModule {
    @Singleton
    @Provides
    @MainDispatcher
    fun provideMainDispatcher(): CoroutineScope {
        return CoroutineScope(Dispatchers.Main)
    }

    @Singleton
    @Provides
    @IoDispatcher
    fun provideIoDispatcher(): CoroutineScope {
        return CoroutineScope(Dispatchers.IO)
    }
}