package com.smascaro.trackmixing.di.module

import com.smascaro.trackmixing.base.ui.BaseFragment
import com.smascaro.trackmixing.main.components.progress.view.BottomProgressViewMvc
import com.smascaro.trackmixing.main.components.progress.view.BottomProgressViewMvcImpl
import com.smascaro.trackmixing.main.view.MainActivityViewMvc
import com.smascaro.trackmixing.main.view.MainActivityViewMvcImpl
import com.smascaro.trackmixing.player.view.TrackPlayerViewMvc
import com.smascaro.trackmixing.player.view.TrackPlayerViewMvcImpl
import com.smascaro.trackmixing.trackslist.components.toolbar.view.ToolbarViewMvc
import com.smascaro.trackmixing.trackslist.components.toolbar.view.ToolbarViewMvcImpl
import dagger.Binds
import dagger.Module
import dagger.android.ContributesAndroidInjector

@Module
abstract class MainModule {

    @ContributesAndroidInjector
    abstract fun baseFragment(): BaseFragment

    @Module
    interface Bindings {
        @Binds
        fun provideMainActivityViewMvc(mainActivityViewMvcImpl: MainActivityViewMvcImpl): MainActivityViewMvc

        @Binds
        fun provideTrackPlayerViewMvcImpl(trackPlayerViewMvcImpl: TrackPlayerViewMvcImpl): TrackPlayerViewMvc

        @Binds
        fun provideBottomProgressViewMvc(bottomProgressViewMvcImpl: BottomProgressViewMvcImpl): BottomProgressViewMvc

        @Binds
        fun provideToolbarViewMvc(toolbarViewMvcImpl: ToolbarViewMvcImpl): ToolbarViewMvc
    }
}