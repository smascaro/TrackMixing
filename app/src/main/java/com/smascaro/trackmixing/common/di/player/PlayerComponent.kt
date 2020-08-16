package com.smascaro.trackmixing.common.di.player

import com.smascaro.trackmixing.service.MixPlayerService
import com.smascaro.trackmixing.ui.player.TracksPlayerActivity
import dagger.Subcomponent

@Subcomponent(modules = [PlayerModule::class])
@PlayerScope
interface PlayerComponent {
    @Subcomponent.Factory
    interface Factory {
        fun create(): PlayerComponent
    }

    fun inject(tracksPlayerActivity: TracksPlayerActivity)
    fun inject(mixPlayerService: MixPlayerService)
}