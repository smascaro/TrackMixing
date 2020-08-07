package com.smascaro.trackmixing.common.di

import com.smascaro.trackmixing.ui.main.MainActivityViewMvc
import com.smascaro.trackmixing.ui.main.MainActivityViewMvcImpl
import dagger.Binds
import dagger.Module

@Module
abstract class ViewMvcBuildersModule {
    @Binds
    abstract fun provideMainActivityViewMvc(mainActivityViewMvcImpl: MainActivityViewMvcImpl): MainActivityViewMvc
}