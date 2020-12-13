package com.smascaro.trackmixing.player.di

import android.content.Context
import com.smascaro.trackmixing.base.di.component.BaseComponent
import com.smascaro.trackmixing.playback.di.component.PlaybackComponent
import com.smascaro.trackmixing.player.view.PlayerFragment
import dagger.BindsInstance
import dagger.Component

@Component(
    dependencies = [PlaybackComponent::class, BaseComponent::class],
    modules = [PlayerModule.Bindings::class]
)
@PlayerScope
interface PlayerComponent {
    fun inject(playerFragment: PlayerFragment)

    @Component.Builder
    interface Builder {
        fun withContext(@BindsInstance context: Context): Builder
        fun withBaseComponent(baseComponent: BaseComponent): Builder
        fun withPlaybackComponent(playbackComponent: PlaybackComponent): Builder
        fun build(): PlayerComponent
    }
}