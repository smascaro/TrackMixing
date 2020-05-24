package com.smascaro.trackmixing.common.di

import androidx.navigation.NavController
import com.smascaro.trackmixing.common.NODE_BASE_URL
import com.smascaro.trackmixing.networking.NodeApi
import com.smascaro.trackmixing.networking.NodeDownloadsApi
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class CompositionRoot {
    private lateinit var mRetrofit: Retrofit
    private lateinit var mRetrofitForBinaryFiles: Retrofit


    private fun getRetrofit(): Retrofit {
        if (!this::mRetrofit.isInitialized) {
            mRetrofit = Retrofit.Builder().apply {
                baseUrl(NODE_BASE_URL)
                addConverterFactory(GsonConverterFactory.create())
            }.build()
        }
        return mRetrofit
    }

    private fun getRetrofitForBinaryFiles(): Retrofit {
        if (!this::mRetrofitForBinaryFiles.isInitialized) {
            mRetrofitForBinaryFiles = Retrofit.Builder().apply {
                baseUrl(NODE_BASE_URL)
            }.build()
        }
        return mRetrofitForBinaryFiles
    }
    fun getNodeApi(): NodeApi {
        return getRetrofit().create(NodeApi::class.java)
    }

    fun getNodeDownloadsApi(): NodeDownloadsApi {
        return getRetrofitForBinaryFiles().create(NodeDownloadsApi::class.java)
    }
}

