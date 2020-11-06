package com.smascaro.trackmixing.playback.di.component

import android.content.Context
import com.smascaro.trackmixing.base.di.component.BaseComponent
import com.smascaro.trackmixing.playback.di.SessionScope
import com.smascaro.trackmixing.playback.di.module.PlaybackModule
import com.smascaro.trackmixing.playback.service.MixPlayerService
import com.smascaro.trackmixing.playback.utils.PlaybackSession
import dagger.BindsInstance
import dagger.Component

@Component(
    dependencies = [BaseComponent::class],
    modules = [PlaybackModule::class, PlaybackModule.Bindings::class]
)
@SessionScope
interface PlaybackComponent {
    fun inject(mixPlayerService: MixPlayerService)

    @Component.Builder
    interface Builder {
        fun withContext(@BindsInstance context: Context): Builder
        fun withBaseComponent(baseComponent: BaseComponent): Builder
        fun build(): PlaybackComponent
    }

    val playbackSession: PlaybackSession
}