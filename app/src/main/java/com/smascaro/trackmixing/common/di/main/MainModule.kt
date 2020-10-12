package com.smascaro.trackmixing.common.di.main

import com.smascaro.trackmixing.main.components.player.view.TrackPlayerViewMvc
import com.smascaro.trackmixing.main.components.player.view.TrackPlayerViewMvcImpl
import com.smascaro.trackmixing.main.components.progress.view.BottomProgressViewMvc
import com.smascaro.trackmixing.main.components.progress.view.BottomProgressViewMvcImpl
import com.smascaro.trackmixing.trackslist.view.TracksListViewMvc
import com.smascaro.trackmixing.trackslist.view.TracksListViewMvcImpl
import dagger.Binds
import dagger.Module

@Module
class MainModule {
    @Module
    interface ViewMvcBindings {
        @MainScope
        @Binds
        fun provideTracksListViewMvc(tracksListViewMvcImpl: TracksListViewMvcImpl): TracksListViewMvc

        @MainScope
        @Binds
        fun provideBottomPlayerViewMvc(bottomPlayerViewMvcImpl: TrackPlayerViewMvcImpl): TrackPlayerViewMvc

        @MainScope
        @Binds
        fun provideBottomProgressViewMvc(bottomProgressViewMvcImpl: BottomProgressViewMvcImpl): BottomProgressViewMvc
    }
}
