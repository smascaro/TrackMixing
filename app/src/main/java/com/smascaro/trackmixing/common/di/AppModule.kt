package com.smascaro.trackmixing.common.di

import android.content.Context
import com.bumptech.glide.Glide
import com.bumptech.glide.RequestManager
import com.smascaro.trackmixing.common.data.datasource.dao.DownloadsDao
import com.smascaro.trackmixing.common.data.datasource.dao.DownloadsDatabase
import com.smascaro.trackmixing.common.data.datasource.network.NodeApi
import com.smascaro.trackmixing.common.data.datasource.network.NodeDownloadsApi
import com.smascaro.trackmixing.common.data.datasource.repository.TracksRepository
import com.smascaro.trackmixing.common.data.datasource.repository.TracksRepositoryImpl
import com.smascaro.trackmixing.common.di.main.RetrofitForBinaryData
import com.smascaro.trackmixing.common.di.main.RetrofitForJsonData
import com.smascaro.trackmixing.common.utils.FilesStorageHelper
import com.smascaro.trackmixing.common.utils.NODE_BASE_URL
import com.smascaro.trackmixing.common.utils.NotificationHelper
import com.smascaro.trackmixing.playbackservice.utils.PlaybackSession
import com.smascaro.trackmixing.playbackservice.utils.PlaybackSessionImpl
import com.smascaro.trackmixing.playbackservice.utils.PlayerNotificationHelper
import com.smascaro.trackmixing.player.business.downloadtrack.utils.DownloadNotificationHelper
import dagger.Binds
import dagger.Module
import dagger.Provides
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit
import javax.inject.Singleton

@Module
class AppModule {
    @Singleton
    @Provides
    fun provideGlideInstance(context: Context): RequestManager {
        return Glide.with(context)
    }

    @Singleton
    @Provides
    fun provideDownloadsDatabase(context: Context): DownloadsDatabase {
        return DownloadsDatabase.getDatabase(context)
    }

    @Singleton
    @Provides
    fun provideDownloadsDao(downloadsDatabase: DownloadsDatabase): DownloadsDao {
        return downloadsDatabase.getDao()
    }

    @Singleton
    @Provides
    fun provideFileStorageHelper(context: Context): FilesStorageHelper {
        return FilesStorageHelper(context)
    }

    @Singleton
    @Provides
    @RetrofitForJsonData
    fun provideRetrofitInstanceWithJson(): Retrofit {
        return Retrofit.Builder().apply {
            baseUrl(NODE_BASE_URL)
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
            readTimeout(30L, TimeUnit.SECONDS)
        }.build()
        return Retrofit.Builder().apply {
            baseUrl(NODE_BASE_URL)
            client(client)
        }.build()
    }

    @Singleton
    @Provides
    fun provideNodeApiForBinaryDownloads(@RetrofitForBinaryData retrofit: Retrofit): NodeDownloadsApi {
        return retrofit.create(NodeDownloadsApi::class.java)
    }

    @Singleton
    @Provides
    @DownloadNotificationHelperImplementation
    fun provideDownloadNotificationHelper(context: Context): NotificationHelper {
        return DownloadNotificationHelper(context)
    }

    @Singleton
    @Provides
    @PlayerNotificationHelperImplementation
    fun providePlayerNotificationHelper(
        context: Context,
        requestManager: RequestManager
    ): NotificationHelper {
        return PlayerNotificationHelper(context, requestManager)
    }


    @Module
    interface StaticBindings {
        @Singleton
        @Binds
        fun providePlaybackSession(playbackSessionImpl: PlaybackSessionImpl): PlaybackSession

        @Singleton
        @Binds
        fun provideTracksRepository(tracksRepositoryImpl: TracksRepositoryImpl): TracksRepository
    }
}