package com.smascaro.trackmixing.di.main

import com.smascaro.trackmixing.search.view.SongSearchFragment
import com.smascaro.trackmixing.trackslist.view.TracksListFragment
import dagger.Module
import dagger.android.ContributesAndroidInjector

@Module
abstract class MainFragmentsBuilderModule {
    @ContributesAndroidInjector
    abstract fun contributeSongSearchFragment(): SongSearchFragment

    @ContributesAndroidInjector
    abstract fun contributeTracksListFragment(): TracksListFragment
}