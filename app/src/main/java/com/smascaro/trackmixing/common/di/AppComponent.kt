package com.smascaro.trackmixing.common.di

import android.content.Context
import com.smascaro.trackmixing.common.di.main.MainComponent
import com.smascaro.trackmixing.common.di.player.PlayerComponent
import com.smascaro.trackmixing.common.di.settings.SettingsComponent
import com.smascaro.trackmixing.player.business.downloadtrack.TrackDownloadService
import dagger.BindsInstance
import dagger.Component
import javax.inject.Singleton

@Singleton
@Component(modules = [AppModule::class, AppModule.StaticBindings::class, ViewMvcBuildersModule::class, AppSubcomponents::class])
interface AppComponent {
    @Component.Factory
    interface Factory {
        fun create(@BindsInstance context: Context): AppComponent
    }

    fun mainComponent(): MainComponent.Factory
    fun playerComponent(): PlayerComponent.Factory
    fun settingsComponent(): SettingsComponent.Factory
    fun inject(trackDownloadService: TrackDownloadService)
}