package com.smascaro.trackmixing.common.di.main

import android.content.Context
import com.smascaro.trackmixing.base.di.MainScope
import com.smascaro.trackmixing.base.di.component.BaseComponent
import com.smascaro.trackmixing.common.di.AppModule
import com.smascaro.trackmixing.main.view.MainActivity
import com.smascaro.trackmixing.playbackservice.MixPlayerService
import com.smascaro.trackmixing.trackslist.view.TracksListFragment
import dagger.BindsInstance
import dagger.Component

@Component(
    dependencies = [BaseComponent::class],
    modules = [MainViewMvcBindings::class, MainModule.Bindings::class, AppModule::class, AppModule.StaticBindings::class]
)
@MainScope
interface MainComponent {
    fun inject(mainActivity: MainActivity)
    fun inject(tracksListFragment: TracksListFragment)
    fun inject(mixPlayerService: MixPlayerService)
    // @Subcomponent.Factory
    // interface Factory {
    //     fun create(@BindsInstance baseActivity: BaseActivity): MainComponent
    // }

    @Component.Builder
    interface Builder {
        fun withContext(@BindsInstance context: Context): Builder
        fun withBaseComponent(baseComponent: BaseComponent): Builder
        fun build(): MainComponent
    }
}