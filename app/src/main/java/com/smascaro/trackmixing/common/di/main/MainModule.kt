package com.smascaro.trackmixing.common.di.main

import androidx.navigation.NavController
import androidx.navigation.findNavController
import com.smascaro.trackmixing.R
import com.smascaro.trackmixing.common.NODE_BASE_URL
import com.smascaro.trackmixing.networking.NodeApi
import com.smascaro.trackmixing.networking.NodeDownloadsApi
import com.smascaro.trackmixing.ui.common.BaseActivity
import com.smascaro.trackmixing.ui.details.TrackDetailsViewMvc
import com.smascaro.trackmixing.ui.details.TrackDetailsViewMvcImpl
import com.smascaro.trackmixing.ui.trackslist.TracksListViewMvc
import com.smascaro.trackmixing.ui.trackslist.TracksListViewMvcImpl
import dagger.Binds
import dagger.Module
import dagger.Provides
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

@Module
class MainModule {
    @MainScope
    @Provides
    @RetrofitForJsonData
    fun provideRetrofitInstanceWithJson(): Retrofit {
        return Retrofit.Builder().apply {
            baseUrl(NODE_BASE_URL)
            addConverterFactory(GsonConverterFactory.create())
        }.build()
    }

    @MainScope
    @Provides
    fun provideNodeApi(@RetrofitForJsonData retrofit: Retrofit): NodeApi {
        return retrofit.create(NodeApi::class.java)
    }

    @MainScope
    @Provides
    @RetrofitForBinaryData
    fun provideRetrofitInstanceForBinaryData(): Retrofit {
        return Retrofit.Builder().apply {
            baseUrl(NODE_BASE_URL)
        }.build()
    }

    @MainScope
    @Provides
    fun provideNodeApiForBinaryDownloads(@RetrofitForBinaryData retrofit: Retrofit): NodeDownloadsApi {
        return retrofit.create(NodeDownloadsApi::class.java)
    }

    @MainScope
    @Provides
    fun provideNavController(baseActivity: BaseActivity): NavController {
        return baseActivity.findNavController(R.id.nav_host_fragment)
    }

    @Module
    interface ViewMvcBindings {
        @MainScope
        @Binds
        fun provideTracksListViewMvc(tracksListViewMvcImpl: TracksListViewMvcImpl): TracksListViewMvc

        @MainScope
        @Binds
        fun provideTrackDetailsViewMvcImpl(trackDetailsViewMvcImpl: TrackDetailsViewMvcImpl): TrackDetailsViewMvc
    }
}
