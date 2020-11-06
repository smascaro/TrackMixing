package com.smascaro.trackmixing.base.di.module.navigation

import com.smascaro.trackmixing.base.utils.navigation.NavigationHelper
import com.smascaro.trackmixing.base.utils.navigation.NavigationHelperImpl
import dagger.Binds
import dagger.Module
import javax.inject.Singleton

@Module
interface NavigationModule{
    @Singleton
    @Binds
    fun provideNavigationHelper(navigationHelperImpl: NavigationHelperImpl): NavigationHelper
}