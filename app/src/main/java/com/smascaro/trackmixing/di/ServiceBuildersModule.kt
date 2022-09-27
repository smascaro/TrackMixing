package com.smascaro.trackmixing.di

import com.smascaro.trackmixing.di.main.MainScope
import com.smascaro.trackmixing.di.module.PlaybackModule
import com.smascaro.trackmixing.playback.service.MixPlayerService
import dagger.Module
import dagger.android.ContributesAndroidInjector

@Module
abstract class ServiceBuildersModule {
    @MainScope
    @ContributesAndroidInjector(
        modules = [
            PlaybackModule.Bindings::class
        ]
    )
    abstract fun mixPlayerService(): MixPlayerService
}