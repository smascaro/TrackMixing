package com.smascaro.trackmixing.common.di

import com.smascaro.trackmixing.main.components.toolbar.view.ToolbarViewMvc
import com.smascaro.trackmixing.main.components.toolbar.view.ToolbarViewMvcImpl
import com.smascaro.trackmixing.main.view.MainActivityViewMvc
import com.smascaro.trackmixing.main.view.MainActivityViewMvcImpl
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

    @Binds
    abstract fun provideTracksListItemViewMvc(tracksListItemViewMvcImpl: TracksListItemViewMvcImpl): TracksListItemViewMvc

    @Singleton
    @Binds
    abstract fun provideSearchResultsViewMvc(searchResultsViewMvcImpl: SearchResultsViewMvcImpl): SearchResultsViewMvc

    @Singleton
    @Binds
    abstract fun provideToolbarViewMvc(toolbarViewMvcImpl: ToolbarViewMvcImpl): ToolbarViewMvc
}