package com.smascaro.trackmixing.common.di.main

import com.smascaro.trackmixing.common.view.ui.BaseActivity
import com.smascaro.trackmixing.main.view.MainActivity
import com.smascaro.trackmixing.search.view.SongSearchFragment
import com.smascaro.trackmixing.trackslist.view.TracksListFragment
import dagger.BindsInstance
import dagger.Subcomponent

@Subcomponent(modules = [MainModule::class, MainModule.ViewMvcBindings::class])
@MainScope
interface MainComponent {
    @Subcomponent.Factory
    interface Factory {
        fun create(@BindsInstance baseActivity: BaseActivity): MainComponent
    }

    fun inject(mainActivity: MainActivity)
    fun inject(tracksListFragment: TracksListFragment)
    fun inject(searchFragment: SongSearchFragment)
}