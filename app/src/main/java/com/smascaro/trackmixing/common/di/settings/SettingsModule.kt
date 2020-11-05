package com.smascaro.trackmixing.common.di.settings

import android.content.Context
import com.smascaro.trackmixing.common.di.main.MainScope
import com.smascaro.trackmixing.common.utils.FilesStorageHelper
import com.smascaro.trackmixing.settings.business.downloadtestdata.download.view.DownloadTestDataViewMvc
import com.smascaro.trackmixing.settings.business.downloadtestdata.download.view.DownloadTestDataViewMvcImpl
import com.smascaro.trackmixing.settings.business.downloadtestdata.selection.view.SelectTestDataViewMvc
import com.smascaro.trackmixing.settings.business.downloadtestdata.selection.view.SelectTestDataViewMvcImpl
import com.smascaro.trackmixing.settings.view.SettingsActivityViewMvc
import com.smascaro.trackmixing.settings.view.SettingsActivityViewMvcImpl
import dagger.Binds
import dagger.Module
import dagger.Provides

@Module
class SettingsModule {

    @MainScope
    @Provides
    fun provideFileStorageHelper(context: Context): FilesStorageHelper {
        return FilesStorageHelper(context)
    }
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

        //
        // @MainScope
        // @Binds
        // fun provideNavigationHelper(navigationHelperImpl: NavigationHelperImpl): NavigationHelper
    }
}