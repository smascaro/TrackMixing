package com.smascaro.trackmixing.di.settings

import com.smascaro.trackmixing.settings.testdata.download.view.DownloadTestDataViewMvcImpl
import com.smascaro.trackmixing.settings.view.SettingsActivityViewMvc
import com.smascaro.trackmixing.settings.view.SettingsActivityViewMvcImpl
import com.smascaro.trackmixing.settingsOld.testdata.download.view.DownloadTestDataViewMvc
import dagger.Binds
import dagger.Module

@Module
abstract class SettingsModule {

    @Module
    interface Bindings {

        @Binds
        fun provideDownloadTestDataViewMvcImpl(downloadTestDataViewMvcImpl: DownloadTestDataViewMvcImpl): DownloadTestDataViewMvc

        @Binds
        fun provideSettingsActivityViewMvcImpl(settingsActivityViewMvcImpl: SettingsActivityViewMvcImpl): SettingsActivityViewMvc
    }
}