package com.smascaro.trackmixing.common.di.main

import com.smascaro.trackmixing.ui.main.MainActivity
import com.smascaro.trackmixing.ui.trackslist.TracksListFragment
import dagger.Subcomponent

@Subcomponent(modules = [MainModule::class])
@MainScope
interface MainComponent {
    @Subcomponent.Factory
    interface Factory {
        fun create(): MainComponent
    }

    fun inject(mainActivity: MainActivity)
    fun inject(tracksListFragment: TracksListFragment)
}