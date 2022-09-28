package com.smascaro.trackmixing.di.main

import androidx.lifecycle.ViewModel
import com.smascaro.trackmixing.di.ViewModelKey
import com.smascaro.trackmixing.search.view.SearchViewModel
import com.smascaro.trackmixing.trackslist.controller.TracksListViewModel
import dagger.Binds
import dagger.Module
import dagger.multibindings.IntoMap

@Module
abstract class MainViewModelsModule {

    @Binds
    @IntoMap
    @ViewModelKey(SearchViewModel::class)
    abstract fun bindSearchViewModel(testDataViewModel: SearchViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(TracksListViewModel::class)
    abstract fun bindTracksListViewModel(tracksListViewModel: TracksListViewModel): ViewModel
}