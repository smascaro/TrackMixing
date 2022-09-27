package com.smascaro.trackmixing.di

import android.content.Context
import com.smascaro.trackmixing.TrackMixingApplication
import com.smascaro.trackmixing.di.module.ViewModelFactoryModule
import com.smascaro.trackmixing.di.module.CoroutineScopesModule
import com.smascaro.trackmixing.di.module.EventBusModule
import com.smascaro.trackmixing.di.module.GlideModule
import com.smascaro.trackmixing.di.module.network.NetworkModule
import com.smascaro.trackmixing.di.module.RepositoryModule
import com.smascaro.trackmixing.di.module.UtilsModule
import com.smascaro.trackmixing.di.module.PlaybackModule
import com.smascaro.trackmixing.di.settings.SettingsModule
import dagger.BindsInstance
import dagger.Component
import dagger.android.AndroidInjectionModule
import dagger.android.AndroidInjector
import javax.inject.Singleton

@Singleton
@Component(
    modules = [
        AndroidInjectionModule::class,
        ViewModelFactoryModule::class,
        ActivityBuildersModule::class,
        CoroutineScopesModule::class,
        EventBusModule::class,
        GlideModule::class,
        NetworkModule::class,
        RepositoryModule::class,
        RepositoryModule.Bindings::class,
        UtilsModule::class,
        PlaybackModule::class,
        PlaybackModule.Bindings::class,
        SettingsModule.Bindings::class,
    ]
)
interface ApplicationComponent : AndroidInjector<TrackMixingApplication> {
    @Component.Factory
    interface Factory {
        fun create(@BindsInstance applicationContext: Context): ApplicationComponent
    }
}