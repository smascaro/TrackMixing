package com.smascaro.trackmixing.di.module

import dagger.Module
import dagger.Provides
import org.greenrobot.eventbus.EventBus
import javax.inject.Singleton

@Module
class EventBusModule {
    @Singleton
    @Provides
    fun provideEventBus(): EventBus {
        return EventBus.getDefault()
    }
}