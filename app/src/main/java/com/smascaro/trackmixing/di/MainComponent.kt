package com.smascaro.trackmixing.di

import android.content.Context
import com.smascaro.trackmixing.base.di.MainScope
import com.smascaro.trackmixing.base.di.component.BaseComponent
import com.smascaro.trackmixing.main.view.MainActivity
import com.smascaro.trackmixing.di.component.PlaybackComponent
import com.smascaro.trackmixing.trackslist.view.TracksListFragment
import dagger.BindsInstance
import dagger.Component

@Component(
    dependencies = [PlaybackComponent::class, BaseComponent::class],
    modules = [MainModule::class, MainModule.Bindings::class]
)
@MainScope
interface MainComponent {
    fun inject(mainActivity: MainActivity)
    fun inject(tracksListFragment: TracksListFragment)

    @Component.Builder
    interface Builder {
        fun withContext(@BindsInstance context: Context): Builder
        fun withBaseComponent(baseComponent: BaseComponent): Builder
        fun withPlaybackComponent(playbackComponent: PlaybackComponent): Builder
        fun build(): MainComponent
    }
}