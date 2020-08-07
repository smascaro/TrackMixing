package com.smascaro.trackmixing.common.di

import android.content.Context
import com.smascaro.trackmixing.common.di.main.MainComponent
import com.smascaro.trackmixing.common.di.main.MainModule
import com.smascaro.trackmixing.ui.main.MainActivity
import dagger.BindsInstance
import dagger.Component
import javax.inject.Singleton

@Singleton
@Component(modules = [AppModule::class, MainModule::class, AppSubcomponents::class])
interface AppComponent {

    @Component.Factory
    interface Factory {
        fun create(@BindsInstance context: Context): AppComponent
    }

    fun mainComponent(): MainComponent.Factory
    fun inject(mainActivity: MainActivity)
}