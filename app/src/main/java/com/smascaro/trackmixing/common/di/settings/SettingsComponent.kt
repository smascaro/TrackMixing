package com.smascaro.trackmixing.common.di.settings

import android.content.Context
import com.smascaro.trackmixing.base.di.component.BaseComponent
import com.smascaro.trackmixing.common.di.AppModule
import com.smascaro.trackmixing.common.di.main.MainScope
import com.smascaro.trackmixing.settings.business.downloadtestdata.DownloadTestDataActivity
import com.smascaro.trackmixing.settings.business.downloadtestdata.download.view.DownloadTestDataFragment
import com.smascaro.trackmixing.settings.business.downloadtestdata.selection.view.SelectTestDataFragment
import com.smascaro.trackmixing.settings.view.SettingsActivity
import dagger.BindsInstance
import dagger.Component

@Component(
    dependencies = [BaseComponent::class],
    modules = [SettingsModule::class, SettingsModule.StaticBindings::class, AppModule.StaticBindings::class]
)
@MainScope
interface SettingsComponent {
    // @Subcomponent.Factory
    // interface Factory {
    //     fun create(): SettingsComponent
    // }

    @Component.Builder
    interface Builder {
        fun withContext(@BindsInstance context: Context): Builder
        fun withBaseComponent(baseComponent: BaseComponent): Builder
        fun build(): SettingsComponent
    }

    fun inject(settingsActivity: SettingsActivity)
    fun inject(downloadTestDataActivity: DownloadTestDataActivity)
    fun inject(selectTestDataFragment: SelectTestDataFragment)
    fun inject(downloadTestDataFragment: DownloadTestDataFragment)
}
