package com.smascaro.trackmixing.common.di.settings

import com.smascaro.trackmixing.common.utils.DRIVE_DOWNLOAD_DATA_BASE_URL
import com.smascaro.trackmixing.settings.business.downloadtestdata.selection.view.SelectTestDataViewMvc
import com.smascaro.trackmixing.settings.business.downloadtestdata.selection.view.SelectTestDataViewMvcImpl
import com.smascaro.trackmixing.settings.business.downloadtestdata.usecase.data.TestDataApi
import dagger.Binds
import dagger.Module
import dagger.Provides
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import java.util.concurrent.TimeUnit


@Module
class SettingsModule {
    @SettingsScope
    @Provides
    fun provideRetrofit(): Retrofit {
        val client = OkHttpClient.Builder().apply {
            readTimeout(10, TimeUnit.SECONDS)
        }.build()
        return Retrofit.Builder().apply {
            baseUrl(DRIVE_DOWNLOAD_DATA_BASE_URL)
            client(client)
        }.build()
    }

    @SettingsScope
    @Provides
    fun provideTestDataApi(retrofit: Retrofit): TestDataApi {
        return retrofit.create(TestDataApi::class.java)
    }

    @Module
    interface StaticBindings {
        @SettingsScope
        @Binds
        fun provideSelectTestDataViewMvcImpl(selectTestDataViewMvcImpl: SelectTestDataViewMvcImpl): SelectTestDataViewMvc
    }
}