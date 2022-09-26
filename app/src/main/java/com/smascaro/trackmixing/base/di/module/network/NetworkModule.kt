package com.smascaro.trackmixing.base.di.module.network

import com.smascaro.trackmixing.base.network.node.api.NodeApi
import com.smascaro.trackmixing.base.network.node.api.NodeContract
import com.smascaro.trackmixing.base.network.node.api.NodeDownloadsApi
import com.smascaro.trackmixing.base.network.testdata.api.AwsContract
import com.smascaro.trackmixing.base.network.testdata.api.TestDataApi
import com.smascaro.trackmixing.base.network.testdata.api.TestDataFilesApi
import com.smascaro.trackmixing.base.network.youtube.api.YoutubeApi
import com.smascaro.trackmixing.base.network.youtube.api.YoutubeContract
import dagger.Module
import dagger.Provides
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit
import javax.inject.Singleton

@Module
class NetworkModule {
    @Singleton
    @Provides
    @RetrofitForYoutubeApi
    fun provideRetrofitInstanceForYoutubeApi(): Retrofit {
        return Retrofit.Builder().apply {
            baseUrl(YoutubeContract.BASE_URL)
            addConverterFactory(GsonConverterFactory.create())
        }.build()
    }

    @Singleton
    @Provides
    fun provideYoutubeApi(@RetrofitForYoutubeApi retrofit: Retrofit): YoutubeApi {
        return retrofit.create(YoutubeApi::class.java)
    }

    @Singleton
    @Provides
    @RetrofitForJsonData
    fun provideRetrofitInstanceWithJson(): Retrofit {
        return Retrofit.Builder().apply {
            baseUrl(NodeContract.BASE_URL)
            client(
                OkHttpClient.Builder().readTimeout(120, TimeUnit.SECONDS)
                    .writeTimeout(3, TimeUnit.SECONDS).build()
            )
            addConverterFactory(GsonConverterFactory.create())
        }.build()
    }

    @Singleton
    @Provides
    fun provideNodeApi(@RetrofitForJsonData retrofit: Retrofit): NodeApi {
        return retrofit.create(NodeApi::class.java)
    }

    @Singleton
    @Provides
    @RetrofitForBinaryData
    fun provideRetrofitInstanceForBinaryData(): Retrofit {
        val client = OkHttpClient.Builder().apply {
            readTimeout(30, TimeUnit.SECONDS)
        }.build()
        return Retrofit.Builder().apply {
            baseUrl(NodeContract.BASE_URL)
            client(client)
        }.build()
    }

    @Singleton
    @Provides
    fun provideNodeApiForBinaryDownloads(@RetrofitForBinaryData retrofit: Retrofit): NodeDownloadsApi {
        return retrofit.create(NodeDownloadsApi::class.java)
    }

    @Singleton
    @RetrofitForInfoBundleFile
    @Provides
    fun provideRetrofitForInfoFile(): Retrofit {
        val client = OkHttpClient.Builder().apply {
            readTimeout(10, TimeUnit.SECONDS)
        }.build()
        return Retrofit.Builder().apply {
            baseUrl(AwsContract.INFO_FILE_URL)
            addConverterFactory(GsonConverterFactory.create())
            client(client)
        }.build()
    }

    @Singleton
    @Provides
    fun provideTestDataApi(@RetrofitForInfoBundleFile retrofit: Retrofit): TestDataApi {
        return retrofit.create(TestDataApi::class.java)
    }

    @Singleton
    @RetrofitForZipBundleFiles
    @Provides
    fun provideRetrofit(): Retrofit {
        val client = OkHttpClient.Builder().apply {
            readTimeout(10, TimeUnit.SECONDS)
        }.build()
        return Retrofit.Builder().apply {
            baseUrl(AwsContract.BUNDLES_BASE_URL)
            client(client)
        }.build()
    }

    @Singleton
    @Provides
    fun provideTestDataFilesApi(@RetrofitForZipBundleFiles retrofit: Retrofit): TestDataFilesApi {
        return retrofit.create(TestDataFilesApi::class.java)
    }
}