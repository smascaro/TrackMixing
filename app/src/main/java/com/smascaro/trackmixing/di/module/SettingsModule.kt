package com.smascaro.trackmixing.di.module

import com.smascaro.trackmixing.base.di.MainScope
import com.smascaro.trackmixing.settings.testdata.download.view.DownloadTestDataViewMvcImpl
import com.smascaro.trackmixing.settings.view.SettingsActivityViewMvc
import com.smascaro.trackmixing.settings.view.SettingsActivityViewMvcImpl
import com.smascaro.trackmixing.settingsOld.testdata.download.view.DownloadTestDataViewMvc
import dagger.Binds
import dagger.Module

@Module
class SettingsModule {

    @Module
    interface StaticBindings {

        @MainScope
        @Binds
        fun provideDownloadTestDataViewMvcImpl(downloadTestDataViewMvcImpl: DownloadTestDataViewMvcImpl): DownloadTestDataViewMvc

        @MainScope
        @Binds
        fun provideSettingsActivityViewMvcImpl(settingsActivityViewMvcImpl: SettingsActivityViewMvcImpl): SettingsActivityViewMvc
    }
}