package com.smascaro.trackmixing.base.di.module.utils

import android.content.Context
import com.smascaro.trackmixing.base.utils.FilesStorageHelper
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@Module
class UtilsModule {

    @Singleton
    @Provides
    fun provideFileStorageHelper(context: Context): FilesStorageHelper {
        return FilesStorageHelper(context)
    }
}