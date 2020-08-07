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
class AppModule