package com.smascaro.trackmixing.common.di

import com.smascaro.trackmixing.main.view.MainActivityViewMvc
import com.smascaro.trackmixing.main.view.MainActivityViewMvcImpl
import com.smascaro.trackmixing.player.view.TracksPlayerViewMvc
import com.smascaro.trackmixing.player.view.TracksPlayerViewMvcImpl
import com.smascaro.trackmixing.search.view.SearchResultsViewMvc
import com.smascaro.trackmixing.search.view.SearchResultsViewMvcImpl
import com.smascaro.trackmixing.trackslist.view.listitem.TracksListItemViewMvc
import com.smascaro.trackmixing.trackslist.view.listitem.TracksListItemViewMvcImpl
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

    @Binds
    abstract fun provideTracksListItemViewMvc(tracksListItemViewMvcImpl: TracksListItemViewMvcImpl): TracksListItemViewMvc

    @Singleton
    @Binds
    abstract fun provideSearchResultsViewMvc(searchResultsViewMvcImpl: SearchResultsViewMvcImpl): SearchResultsViewMvc
}