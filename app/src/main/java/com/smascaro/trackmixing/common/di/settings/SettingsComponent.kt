package com.smascaro.trackmixing.common.di.settings

import com.smascaro.trackmixing.settings.business.downloadtestdata.DownloadTestDataActivity
import com.smascaro.trackmixing.settings.business.downloadtestdata.download.view.DownloadTestDataFragment
import com.smascaro.trackmixing.settings.business.downloadtestdata.selection.view.SelectTestDataFragment
import com.smascaro.trackmixing.settings.view.SettingsActivity
import dagger.Subcomponent

@Subcomponent(modules = [SettingsModule::class, SettingsModule.StaticBindings::class])
@SettingsScope
interface SettingsComponent {
    @Subcomponent.Factory
    interface Factory {
        fun create(): SettingsComponent
    }

    fun inject(settingsActivity: SettingsActivity)
    fun inject(downloadTestDataActivity: DownloadTestDataActivity)
    fun inject(selectTestDataFragment: SelectTestDataFragment)
    fun inject(downloadTestDataFragment: DownloadTestDataFragment)
}
