package com.smascaro.trackmixing.di.component

import android.content.Context
import com.smascaro.trackmixing.base.di.MainScope
import com.smascaro.trackmixing.base.di.component.BaseComponent
import com.smascaro.trackmixing.di.factories.TestDataViewModelFactory
import com.smascaro.trackmixing.di.module.SettingsModule
import com.smascaro.trackmixing.settings.testdata.download.view.DownloadTestDataActivity
import com.smascaro.trackmixing.settings.testdata.download.view.DownloadTestDataFragment
import com.smascaro.trackmixing.settings.testdata.selection.view.SelectTestDataFragment
import com.smascaro.trackmixing.settings.view.SettingsActivity
import dagger.BindsInstance
import dagger.Component

@Component(
    dependencies = [BaseComponent::class],
    modules = [SettingsModule::class, SettingsModule.StaticBindings::class]
)
@MainScope
interface SettingsComponent {
    fun inject(settingsActivity: SettingsActivity)
    fun inject(downloadTestDataActivity: DownloadTestDataActivity)
    fun inject(selectTestDataFragment: SelectTestDataFragment)
    fun inject(downloadTestDataFragment: DownloadTestDataFragment)

    fun viewModelFactory(): TestDataViewModelFactory

    @Component.Builder
    interface Builder {
        fun withContext(@BindsInstance context: Context): Builder
        fun withBaseComponent(baseComponent: BaseComponent): Builder
        fun build(): SettingsComponent
    }
}
