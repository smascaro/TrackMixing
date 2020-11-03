package com.smascaro.trackmixing.common.di

import android.content.Context
import com.bumptech.glide.Glide
import com.bumptech.glide.RequestManager
import com.smascaro.trackmixing.common.data.datasource.network.NodeApi
import com.smascaro.trackmixing.common.data.datasource.network.NodeDownloadsApi
import com.smascaro.trackmixing.common.data.datasource.repository.DownloadsDao
import com.smascaro.trackmixing.common.data.datasource.repository.DownloadsDatabase
import com.smascaro.trackmixing.common.data.datasource.repository.TracksRepository
import com.smascaro.trackmixing.common.data.datasource.repository.TracksRepositoryImpl
import com.smascaro.trackmixing.common.data.network.api.NodeContract
import com.smascaro.trackmixing.common.data.network.api.YoutubeContract
import com.smascaro.trackmixing.common.di.coroutines.MainCoroutineScope
import com.smascaro.trackmixing.common.di.main.RetrofitForBinaryData
import com.smascaro.trackmixing.common.di.main.RetrofitForJsonData
import com.smascaro.trackmixing.common.di.main.RetrofitForYoutubeApi
import com.smascaro.trackmixing.common.utils.FilesStorageHelper
import com.smascaro.trackmixing.common.utils.navigation.NavigationHelper
import com.smascaro.trackmixing.common.utils.navigation.NavigationHelperImpl
import com.smascaro.trackmixing.common.utils.ui.NotificationHelper
import com.smascaro.trackmixing.playbackservice.utils.PlaybackSession
import com.smascaro.trackmixing.playbackservice.utils.PlaybackSessionImpl
import com.smascaro.trackmixing.playbackservice.utils.PlayerNotificationHelper
import com.smascaro.trackmixing.player.business.downloadtrack.utils.DownloadNotificationHelper
import com.smascaro.trackmixing.search.model.repository.YoutubeApi
import dagger.Binds
import dagger.Module
import dagger.Provides
import okhttp3.OkHttpClient
import org.greenrobot.eventbus.EventBus
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
    @DownloadNotificationHelperImplementation
    fun provideDownloadNotificationHelper(context: Context): NotificationHelper {
        return DownloadNotificationHelper(context)
    }

    @Singleton
    @Provides
    @PlayerNotificationHelperImplementation
    fun providePlayerNotificationHelper(
        context: Context,
        requestManager: RequestManager,
        ui: MainCoroutineScope
    ): NotificationHelper {
        return PlayerNotificationHelper(context, requestManager, ui)
    }

    @Singleton
    @Provides
    fun provideEventBus(): EventBus {
        return EventBus.getDefault()
    }

    @Module
    interface StaticBindings {
        @Singleton
        @Binds
        fun providePlaybackSession(playbackSessionImpl: PlaybackSessionImpl): PlaybackSession

        @Singleton
        @Binds
        fun provideTracksRepository(tracksRepositoryImpl: TracksRepositoryImpl): TracksRepository

        @Singleton
        @Binds
        fun provideNavigationHelper(navigationHelperImpl: NavigationHelperImpl): NavigationHelper
    }
}