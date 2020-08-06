package com.smascaro.trackmixing.common.di

import android.content.Context
import com.smascaro.trackmixing.common.TrackMixingApplication
import com.smascaro.trackmixing.common.di.main.MainModule
import com.smascaro.trackmixing.ui.main.MainActivity
import com.smascaro.trackmixing.ui.trackslist.trackslistitem.TracksListItemViewMvcImpl
import dagger.BindsInstance
import dagger.Component
import dagger.android.AndroidInjectionModule
import dagger.android.AndroidInjector
import javax.inject.Singleton

@Singleton
@Component(modules = [AppModule::class, MainModule::class])
interface AppComponent {

    @Component.Factory
    interface Factory {
        fun create(@BindsInstance context: Context): AppComponent
    }

    fun inject(mainActivity: MainActivity)
}