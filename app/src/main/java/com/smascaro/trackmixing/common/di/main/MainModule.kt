package com.smascaro.trackmixing.common.di.main

import com.smascaro.trackmixing.playbackservice.utils.PlaybackSession
import com.smascaro.trackmixing.playbackservice.utils.PlaybackSessionImpl
import dagger.Binds
import dagger.Module

@Module
class MainModule {
    @Module
    interface Bindings {
        @MainScope
        @Binds
        fun providePlaybackSession(playbackSessionImpl: PlaybackSessionImpl): PlaybackSession
    }
}