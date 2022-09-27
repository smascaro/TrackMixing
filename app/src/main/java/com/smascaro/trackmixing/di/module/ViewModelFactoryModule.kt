package com.smascaro.trackmixing.di.module

import androidx.lifecycle.ViewModelProvider
import com.smascaro.trackmixing.di.ViewModelProviderFactory
import dagger.Binds
import dagger.Module

@Module
interface ViewModelFactoryModule {
    @Binds
    fun bindViewModelFactory(factory: ViewModelProviderFactory): ViewModelProvider.Factory
}