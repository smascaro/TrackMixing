package com.smascaro.trackmixing.common.di

import android.content.Context
import com.bumptech.glide.Glide
import com.bumptech.glide.RequestManager
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@Module
class AppModule {
    @Singleton
    @Provides
    fun provideGlideInstance(context: Context): RequestManager {
        return Glide.with(context)
    }
}