package com.smascaro.trackmixing.di.settings

import com.smascaro.trackmixing.settings.view.SettingsActivityViewMvc
import com.smascaro.trackmixing.settings.view.SettingsActivityViewMvcImpl
import dagger.Binds
import dagger.Module

@Module
abstract class SettingsModule {

    @Module
    interface Bindings {

        @Binds
        fun provideSettingsActivityViewMvcImpl(settingsActivityViewMvcImpl: SettingsActivityViewMvcImpl): SettingsActivityViewMvc
    }
}