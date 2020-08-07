package com.smascaro.trackmixing.common.di

import com.smascaro.trackmixing.common.NODE_BASE_URL
import com.smascaro.trackmixing.common.di.main.RetrofitForJsonData
import com.smascaro.trackmixing.networking.NodeApi
import dagger.Module
import dagger.Provides
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton

@Module
class AppModule {
    @Singleton
    @Provides
    @RetrofitForJsonData
    fun provideRetrofitInstanceWithJson(): Retrofit {
        return Retrofit.Builder().apply {
            baseUrl(NODE_BASE_URL)
            addConverterFactory(GsonConverterFactory.create())
            build()
        }.build()
    }

    @Singleton
    @Provides
    fun provideNodeApi(@RetrofitForJsonData retrofit: Retrofit): NodeApi {
        return retrofit.create(NodeApi::class.java)
    }
}