package com.smascaro.trackmixing.common.di

import com.smascaro.trackmixing.ui.main.MainActivityViewMvc
import com.smascaro.trackmixing.ui.main.MainActivityViewMvcImpl
import dagger.Binds
import dagger.Module
import javax.inject.Singleton

@Module
abstract class ViewMvcBuildersModule {
    @Singleton
    @Binds
    abstract fun provideMainActivityViewMvc(mainActivityViewMvcImpl: MainActivityViewMvcImpl): MainActivityViewMvc
}