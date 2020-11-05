package com.smascaro.trackmixing.common.di.main

import com.smascaro.trackmixing.main.components.player.view.TrackPlayerViewMvc
import com.smascaro.trackmixing.main.components.player.view.TrackPlayerViewMvcImpl
import com.smascaro.trackmixing.main.components.progress.view.BottomProgressViewMvc
import com.smascaro.trackmixing.main.components.progress.view.BottomProgressViewMvcImpl
import com.smascaro.trackmixing.main.view.MainActivityViewMvc
import com.smascaro.trackmixing.main.view.MainActivityViewMvcImpl
import com.smascaro.trackmixing.trackslist.components.toolbar.view.ToolbarViewMvc
import com.smascaro.trackmixing.trackslist.components.toolbar.view.ToolbarViewMvcImpl
import com.smascaro.trackmixing.trackslist.view.TracksListViewMvc
import com.smascaro.trackmixing.trackslist.view.TracksListViewMvcImpl
import dagger.Binds
import dagger.Module

@Module
interface MainViewMvcBindings {
    @MainScope
    @Binds
    fun provideMainActivityViewMvc(mainActivityViewMvcImpl: MainActivityViewMvcImpl): MainActivityViewMvc

    @MainScope
    @Binds
    fun provideTracksListViewMvc(tracksListViewMvcImpl: TracksListViewMvcImpl): TracksListViewMvc

    @MainScope
    @Binds
    fun provideBottomPlayerViewMvc(bottomPlayerViewMvcImpl: TrackPlayerViewMvcImpl): TrackPlayerViewMvc

    @MainScope
    @Binds
    fun provideBottomProgressViewMvc(bottomProgressViewMvcImpl: BottomProgressViewMvcImpl): BottomProgressViewMvc

    @MainScope
    @Binds
    fun provideToolbarViewMvc(toolbarViewMvcImpl: ToolbarViewMvcImpl): ToolbarViewMvc
}

