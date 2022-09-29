package com.smascaro.trackmixing.di

import com.smascaro.trackmixing.di.main.MainFragmentsBuilderModule
import com.smascaro.trackmixing.di.main.MainScope
import com.smascaro.trackmixing.di.main.MainViewModelsModule
import com.smascaro.trackmixing.di.module.MainModule
import com.smascaro.trackmixing.di.module.NavigationModule
import com.smascaro.trackmixing.di.settings.SettingsFragmentsBuilderModule
import com.smascaro.trackmixing.di.settings.SettingsScope
import com.smascaro.trackmixing.di.settings.SettingsViewModelsModule
import com.smascaro.trackmixing.main.view.MainActivity
import com.smascaro.trackmixing.player.PlayerActivity
import com.smascaro.trackmixing.settings.testdata.download.view.DownloadTestDataActivity
import com.smascaro.trackmixing.settings.view.SettingsActivity
import dagger.Module
import dagger.android.ContributesAndroidInjector

@Module
abstract class ActivityBuildersModule {

    @MainScope
    @ContributesAndroidInjector(
        modules = [
            MainViewModelsModule::class,
            MainFragmentsBuilderModule::class,
            MainModule::class,
            MainModule.Bindings::class,
            NavigationModule::class
        ]
    )
    abstract fun contributeMainActivity(): MainActivity

    @SettingsScope
    @ContributesAndroidInjector
    abstract fun contributeSettingsActivity(): SettingsActivity

    @SettingsScope
    @ContributesAndroidInjector(
        modules = [
            SettingsViewModelsModule::class,
            SettingsFragmentsBuilderModule::class,
            NavigationModule::class
        ]
    )
    abstract fun contributeDownloadTestDataActivity(): DownloadTestDataActivity

    @MainScope
    @ContributesAndroidInjector(
        modules = [
            MainModule.Bindings::class,
            NavigationModule::class
        ]
    )
    abstract fun contributePlayerActivity(): PlayerActivity
}