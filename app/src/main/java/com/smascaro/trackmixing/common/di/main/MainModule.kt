package com.smascaro.trackmixing.common.di.main

import com.smascaro.trackmixing.ui.main.MainActivityViewMvc
import com.smascaro.trackmixing.ui.main.MainActivityViewMvcImpl
import dagger.Binds
import dagger.Module

@Module
abstract class MainModule {
    @Binds
    abstract fun provideMainActivityViewMvc(mainActivityViewMvcImpl: MainActivityViewMvcImpl): MainActivityViewMvc
}