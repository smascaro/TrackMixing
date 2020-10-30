package com.smascaro.trackmixing.common.di.settings

import com.smascaro.trackmixing.common.data.network.api.AwsContract
import com.smascaro.trackmixing.settings.business.downloadtestdata.download.view.DownloadTestDataViewMvc
import com.smascaro.trackmixing.settings.business.downloadtestdata.download.view.DownloadTestDataViewMvcImpl
import com.smascaro.trackmixing.settings.business.downloadtestdata.selection.view.SelectTestDataViewMvc
import com.smascaro.trackmixing.settings.business.downloadtestdata.selection.view.SelectTestDataViewMvcImpl
import com.smascaro.trackmixing.settings.business.downloadtestdata.usecase.data.TestDataApi
import com.smascaro.trackmixing.settings.business.downloadtestdata.usecase.data.TestDataFilesApi
import com.smascaro.trackmixing.settings.view.SettingsActivityViewMvc
import com.smascaro.trackmixing.settings.view.SettingsActivityViewMvcImpl
import dagger.Binds
import dagger.Module
import dagger.Provides
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

@Module
class SettingsModule {
    @SettingsScope
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

    @SettingsScope
    @Provides
    fun provideTestDataApi(@RetrofitForInfoBundleFile retrofit: Retrofit): TestDataApi {
        return retrofit.create(TestDataApi::class.java)
    }

    @SettingsScope
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

    @SettingsScope
    @Provides
    fun provideTestDataFilesApi(@RetrofitForZipBundleFiles retrofit: Retrofit): TestDataFilesApi {
        return retrofit.create(TestDataFilesApi::class.java)
    }

    @Module
    interface StaticBindings {
        @SettingsScope
        @Binds
        fun provideSelectTestDataViewMvcImpl(selectTestDataViewMvcImpl: SelectTestDataViewMvcImpl): SelectTestDataViewMvc

        @SettingsScope
        @Binds
        fun provideDownloadTestDataViewMvcImpl(downloadTestDataViewMvcImpl: DownloadTestDataViewMvcImpl): DownloadTestDataViewMvc

        @SettingsScope
        @Binds
        fun provideSettingsActivityViewMvcImpl(settingsActivityViewMvcImpl: SettingsActivityViewMvcImpl): SettingsActivityViewMvc
    }
}