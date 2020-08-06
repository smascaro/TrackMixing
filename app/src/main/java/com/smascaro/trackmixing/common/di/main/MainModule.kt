package com.smascaro.trackmixing.common.di.main

import com.smascaro.trackmixing.networking.NodeApi
import dagger.Module
import dagger.Provides
import retrofit2.Retrofit
import javax.inject.Singleton

@Module
class MainModule {
    @Singleton
    @Provides
    fun provideNodeApi(@RetrofitForJsonData retrofit: Retrofit): NodeApi {
        return retrofit.create(NodeApi::class.java)
    }
}