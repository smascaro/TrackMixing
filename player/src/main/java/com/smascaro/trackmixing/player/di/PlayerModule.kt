package com.smascaro.trackmixing.player.di

import dagger.Binds
import dagger.Module

interface PlayerModule {
    @Module
    interface Bindings {
        @PlayerScope
        @Binds
        fun provideTrackPlayerViewMvc(bottomPlayerViewMvcImpl: com.smascaro.trackmixing.player.view.TrackPlayerViewMvcImpl): com.smascaro.trackmixing.player.view.TrackPlayerViewMvc
    }
}
