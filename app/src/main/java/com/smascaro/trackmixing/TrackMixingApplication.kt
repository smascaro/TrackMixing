package com.smascaro.trackmixing

import android.app.Application
import com.smascaro.trackmixing.base.di.component.BaseComponent
import com.smascaro.trackmixing.base.di.component.BaseComponentProvider
import com.smascaro.trackmixing.base.di.component.DaggerBaseComponent
import com.smascaro.trackmixing.di.DaggerMainComponent
import com.smascaro.trackmixing.di.MainComponent
import com.smascaro.trackmixing.di.MainComponentProvider
import com.smascaro.trackmixing.di.component.DaggerPlaybackComponent
import com.smascaro.trackmixing.di.component.PlaybackComponent
import com.smascaro.trackmixing.di.component.PlaybackComponentProvider
import com.smascaro.trackmixing.di.component.SearchComponent
import com.smascaro.trackmixing.di.component.DaggerSearchComponent
import com.smascaro.trackmixing.di.component.SearchComponentProvider
import com.smascaro.trackmixing.di.component.DaggerSettingsComponent
import com.smascaro.trackmixing.di.component.SettingsComponent
import com.smascaro.trackmixing.di.component.SettingsComponentProvider
import timber.log.Timber

class TrackMixingApplication :
    Application(),
    BaseComponentProvider,
    MainComponentProvider,
    SearchComponentProvider,
    PlaybackComponentProvider,
    SettingsComponentProvider {
    override fun onCreate() {
        super.onCreate()
        if (BuildConfig.DEBUG) {
            Timber.plant(Timber.DebugTree())
        }
    }

    override fun provideBaseComponent(): BaseComponent {
        return DaggerBaseComponent.builder()
            .withContext(applicationContext)
            .build()
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
            .withPlaybackComponent(providePlaybackComponent())
            .withContext(applicationContext)
            .build()
    }

    override fun provideSettingsComponent(): SettingsComponent {
        return DaggerSettingsComponent.builder()
            .withBaseComponent(provideBaseComponent())
            .withContext(applicationContext)
            .build()
    }

    override fun providePlaybackComponent(): PlaybackComponent {
        return DaggerPlaybackComponent.builder()
            .withBaseComponent(provideBaseComponent())
            .withContext(applicationContext)
            .build()
    }
}