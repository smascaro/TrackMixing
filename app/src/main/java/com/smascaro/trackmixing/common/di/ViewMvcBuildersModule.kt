package com.smascaro.trackmixing.common.di

import com.smascaro.trackmixing.common.di.main.MainScope
import com.smascaro.trackmixing.main.view.MainActivityViewMvc
import com.smascaro.trackmixing.main.view.MainActivityViewMvcImpl
import com.smascaro.trackmixing.trackslist.view.listitem.TracksListItemViewMvc
import com.smascaro.trackmixing.trackslist.view.listitem.TracksListItemViewMvcImpl
import dagger.Binds
import dagger.Module

@Module
abstract class ViewMvcBuildersModule {
    @MainScope
    @Binds
    abstract fun provideMainActivityViewMvc(mainActivityViewMvcImpl: MainActivityViewMvcImpl): MainActivityViewMvc

    @Binds
    abstract fun provideTracksListItemViewMvc(tracksListItemViewMvcImpl: TracksListItemViewMvcImpl): TracksListItemViewMvc
}