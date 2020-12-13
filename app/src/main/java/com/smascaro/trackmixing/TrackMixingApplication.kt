package com.smascaro.trackmixing

import android.app.Application
import com.smascaro.trackmixing.base.di.component.BaseComponent
import com.smascaro.trackmixing.base.di.component.BaseComponentProvider
import com.smascaro.trackmixing.base.di.component.DaggerBaseComponent
import com.smascaro.trackmixing.di.DaggerMainComponent
import com.smascaro.trackmixing.di.MainComponent
import com.smascaro.trackmixing.di.MainComponentProvider
import com.smascaro.trackmixing.playback.di.component.DaggerPlaybackComponent
import com.smascaro.trackmixing.playback.di.component.PlaybackComponent
import com.smascaro.trackmixing.playback.di.component.PlaybackComponentProvider
import com.smascaro.trackmixing.player.di.DaggerPlayerComponent
import com.smascaro.trackmixing.player.di.PlayerComponent
import com.smascaro.trackmixing.player.di.PlayerComponentProvider
import com.smascaro.trackmixing.search.di.component.DaggerSearchComponent
import com.smascaro.trackmixing.search.di.component.SearchComponentProvider
import com.smascaro.trackmixing.settings.di.component.DaggerSettingsComponent
import com.smascaro.trackmixing.settings.di.component.SettingsComponent
import com.smascaro.trackmixing.settings.di.component.SettingsComponentProvider
import timber.log.Timber

class TrackMixingApplication :
    Application(),
    BaseComponentProvider,
    MainComponentProvider,
    SearchComponentProvider,
    PlaybackComponentProvider,
    SettingsComponentProvider,
    PlayerComponentProvider {
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

    override fun provideSearchComponent(): com.smascaro.trackmixing.search.di.component.SearchComponent {
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

    override fun providePlayerComponent(): PlayerComponent {
        return DaggerPlayerComponent.builder()
            .withPlaybackComponent(providePlaybackComponent())
            .withBaseComponent(provideBaseComponent())
            .withContext(applicationContext)
            .build()
    }
}