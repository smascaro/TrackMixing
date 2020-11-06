package com.smascaro.trackmixing.settings.di.module

import com.smascaro.trackmixing.base.di.MainScope
import com.smascaro.trackmixing.base.utils.navigation.NavigationHelper
import com.smascaro.trackmixing.settings.controller.SettingsNavigationHelper
import com.smascaro.trackmixing.settings.testdata.download.view.DownloadTestDataViewMvcImpl
import com.smascaro.trackmixing.settings.testdata.selection.view.SelectTestDataViewMvc
import com.smascaro.trackmixing.settings.testdata.selection.view.SelectTestDataViewMvcImpl
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
        fun provideSelectTestDataViewMvcImpl(selectTestDataViewMvcImpl: SelectTestDataViewMvcImpl): SelectTestDataViewMvc

        @MainScope
        @Binds
        fun provideDownloadTestDataViewMvcImpl(downloadTestDataViewMvcImpl: DownloadTestDataViewMvcImpl): DownloadTestDataViewMvc

        @MainScope
        @Binds
        fun provideSettingsActivityViewMvcImpl(settingsActivityViewMvcImpl: SettingsActivityViewMvcImpl): SettingsActivityViewMvc

        @MainScope
        @Binds
        fun provideNavigationHelper(settingsNavigationHelper: SettingsNavigationHelper): NavigationHelper
    }
}