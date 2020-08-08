package com.smascaro.trackmixing.common.di

import com.smascaro.trackmixing.ui.main.MainActivityViewMvc
import com.smascaro.trackmixing.ui.main.MainActivityViewMvcImpl
import com.smascaro.trackmixing.ui.player.TracksPlayerViewMvc
import com.smascaro.trackmixing.ui.player.TracksPlayerViewMvcImpl
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
    abstract fun provideTracksPlayerViewMvc(tracksPlayerViewMvcImpl: TracksPlayerViewMvcImpl): TracksPlayerViewMvc

}