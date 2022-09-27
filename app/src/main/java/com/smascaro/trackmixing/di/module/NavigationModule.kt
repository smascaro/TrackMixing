package com.smascaro.trackmixing.di.module

import com.smascaro.trackmixing.base.utils.navigation.NavigationHelper
import com.smascaro.trackmixing.base.utils.navigation.NavigationHelperImpl
import dagger.Binds
import dagger.Module

@Module
interface NavigationModule {
    @Binds
    fun provideNavigationHelper(navigationHelperImpl: NavigationHelperImpl): NavigationHelper
}