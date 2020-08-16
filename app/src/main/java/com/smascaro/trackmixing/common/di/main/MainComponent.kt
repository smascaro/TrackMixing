package com.smascaro.trackmixing.common.di.main

import com.smascaro.trackmixing.ui.common.BaseActivity
import com.smascaro.trackmixing.ui.details.TrackDetailsFragment
import com.smascaro.trackmixing.ui.main.MainActivity
import com.smascaro.trackmixing.ui.trackslist.TracksListFragment
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
    fun inject(trackDetailsFragment: TrackDetailsFragment)
}