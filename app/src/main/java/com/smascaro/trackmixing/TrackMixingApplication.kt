package com.smascaro.trackmixing

import android.app.Application
import com.smascaro.trackmixing.base.di.component.BaseComponent
import com.smascaro.trackmixing.base.di.component.BaseComponentProvider
import com.smascaro.trackmixing.base.di.component.DaggerBaseComponent
import com.smascaro.trackmixing.common.di.AppComponent
import com.smascaro.trackmixing.common.di.AppComponentProvider
import com.smascaro.trackmixing.common.di.DaggerAppComponent
import com.smascaro.trackmixing.common.di.main.DaggerMainComponent
import com.smascaro.trackmixing.common.di.main.MainComponent
import com.smascaro.trackmixing.common.di.main.MainComponentProvider
import com.smascaro.trackmixing.common.di.search.DaggerSearchComponent
import com.smascaro.trackmixing.common.di.search.SearchComponent
import com.smascaro.trackmixing.common.di.search.SearchComponentProvider
import com.smascaro.trackmixing.common.di.settings.DaggerSettingsComponent
import com.smascaro.trackmixing.common.di.settings.SettingsComponent
import com.smascaro.trackmixing.common.di.settings.SettingsComponentProvider
import timber.log.Timber

class TrackMixingApplication :
    Application(),
    BaseComponentProvider,
    AppComponentProvider,
    MainComponentProvider,
    SearchComponentProvider,
    SettingsComponentProvider {
    override fun onCreate() {
        super.onCreate()
        if (BuildConfig.DEBUG) {
            Timber.plant(Timber.DebugTree())
        }
    }

    override fun provideBaseComponent(): BaseComponent {
        return DaggerBaseComponent.builder().withContext(applicationContext).build()
    }

    override fun provideSearchComponent(): SearchComponent {
        return DaggerSearchComponent.builder()
            .withBaseComponent(provideBaseComponent())
            .withContext(applicationContext)
            .build()
    }

    override fun provideMainComponent(): MainComponent {
        return DaggerMainComponent.builder()
            .withBaseComponent(provideBaseComponent())
            .withContext(applicationContext)
            .build()
    }

    override fun provideAppComponent(): AppComponent {
        return DaggerAppComponent.builder().baseComponent(provideBaseComponent())
            .build()
    }

    override fun provideSettingsComponent(): SettingsComponent {
        return DaggerSettingsComponent.builder()
            .withBaseComponent(provideBaseComponent())
            .withContext(applicationContext)
            .build()
    }
}