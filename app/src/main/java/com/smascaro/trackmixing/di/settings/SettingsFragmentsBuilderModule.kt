package com.smascaro.trackmixing.di.settings

import com.smascaro.trackmixing.settings.testdata.download.view.DownloadTestDataFragment
import com.smascaro.trackmixing.settings.testdata.selection.view.SelectTestDataFragment
import dagger.Module
import dagger.android.ContributesAndroidInjector

@Module
abstract class SettingsFragmentsBuilderModule {
    @ContributesAndroidInjector
    abstract fun contributeSelectTestDataFragment(): SelectTestDataFragment

    @ContributesAndroidInjector
    abstract fun contributeDownloadTestDataFragment(): DownloadTestDataFragment
}