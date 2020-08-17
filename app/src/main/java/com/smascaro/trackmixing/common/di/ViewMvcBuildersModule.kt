package com.smascaro.trackmixing.common.di

import com.smascaro.trackmixing.ui.main.BottomPlayerViewMvc
import com.smascaro.trackmixing.ui.main.BottomPlayerViewMvcImpl
import com.smascaro.trackmixing.ui.main.MainActivityViewMvc
import com.smascaro.trackmixing.ui.main.MainActivityViewMvcImpl
import com.smascaro.trackmixing.ui.player.TracksPlayerViewMvc
import com.smascaro.trackmixing.ui.player.TracksPlayerViewMvcImpl
import com.smascaro.trackmixing.ui.trackslist.trackslistitem.TracksListItemViewMvc
import com.smascaro.trackmixing.ui.trackslist.trackslistitem.TracksListItemViewMvcImpl
import dagger.Binds
import dagger.Module
import javax.inject.Singleton

@Module
abstract class ViewMvcBuildersModule {
    @Singleton
    @Binds
    abstract fun provideMainActivityViewMvc(mainActivityViewMvcImpl: MainActivityViewMvcImpl): MainActivityViewMvc

    @Singleton
    @Binds
    abstract fun provideBottomPlayerViewMvc(bottomPlayerViewMvcImpl: BottomPlayerViewMvcImpl): BottomPlayerViewMvc

    @Singleton
    @Binds
    abstract fun provideTracksPlayerViewMvc(tracksPlayerViewMvcImpl: TracksPlayerViewMvcImpl): TracksPlayerViewMvc

    @Binds
    abstract fun provideTracksListItemViewMvc(tracksListItemViewMvcImpl: TracksListItemViewMvcImpl): TracksListItemViewMvc
}