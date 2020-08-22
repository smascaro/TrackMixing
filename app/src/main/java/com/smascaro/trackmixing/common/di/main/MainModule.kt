package com.smascaro.trackmixing.common.di.main

import com.smascaro.trackmixing.details.view.TrackDetailsViewMvc
import com.smascaro.trackmixing.details.view.TrackDetailsViewMvcImpl
import com.smascaro.trackmixing.main.components.bottomplayer.view.BottomPlayerViewMvc
import com.smascaro.trackmixing.main.components.bottomplayer.view.BottomPlayerViewMvcImpl
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
        fun provideTrackDetailsViewMvcImpl(trackDetailsViewMvcImpl: TrackDetailsViewMvcImpl): TrackDetailsViewMvc

        @MainScope
        @Binds
        fun provideBottomPlayerViewMvc(bottomPlayerViewMvcImpl: BottomPlayerViewMvcImpl): BottomPlayerViewMvc

        @MainScope
        @Binds
        fun provideBottomProgressViewMvc(bottomProgressViewMvcImpl: BottomProgressViewMvcImpl): BottomProgressViewMvc
    }
}
