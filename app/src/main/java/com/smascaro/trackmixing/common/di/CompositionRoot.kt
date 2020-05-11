package com.smascaro.trackmixing.common.di

import com.smascaro.trackmixing.common.NODE_BASE_URL
import com.smascaro.trackmixing.networking.NodeApi
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class CompositionRoot {
    private lateinit var mRetrofit: Retrofit

    fun getRetrofit(): Retrofit {
        if (!this::mRetrofit.isInitialized) {
            mRetrofit = Retrofit.Builder().apply {
                baseUrl(NODE_BASE_URL)
                addConverterFactory(GsonConverterFactory.create())
            }.build()
        }
        return mRetrofit
    }

    fun getNodeApi(): NodeApi {
        return getRetrofit().create(NodeApi::class.java)
    }
}

