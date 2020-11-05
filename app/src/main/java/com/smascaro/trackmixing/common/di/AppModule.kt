package com.smascaro.trackmixing.common.di

import android.content.Context
import com.bumptech.glide.RequestManager
import com.smascaro.trackmixing.base.coroutine.MainCoroutineScope
import com.smascaro.trackmixing.common.data.datasource.repository.DownloadsDao
import com.smascaro.trackmixing.common.data.datasource.repository.DownloadsDatabase
import com.smascaro.trackmixing.common.data.datasource.repository.TracksRepository
import com.smascaro.trackmixing.common.data.datasource.repository.TracksRepositoryImpl
import com.smascaro.trackmixing.common.di.main.MainScope
import com.smascaro.trackmixing.common.utils.FilesStorageHelper
import com.smascaro.trackmixing.common.utils.navigation.NavigationHelper
import com.smascaro.trackmixing.common.utils.navigation.NavigationHelperImpl
import com.smascaro.trackmixing.common.utils.ui.NotificationHelper
import com.smascaro.trackmixing.playbackservice.utils.PlaybackSession
import com.smascaro.trackmixing.playbackservice.utils.PlayerNotificationHelper
import com.smascaro.trackmixing.search.business.download.utils.DownloadNotificationHelper
import dagger.Binds
import dagger.Module
import dagger.Provides
import org.greenrobot.eventbus.EventBus

@Module
class AppModule {

    @MainScope
    @Provides
    fun provideDownloadsDatabase(context: Context): DownloadsDatabase {
        return DownloadsDatabase.getDatabase(context)
    }

    @MainScope
    @Provides
    fun provideDownloadsDao(downloadsDatabase: DownloadsDatabase): DownloadsDao {
        return downloadsDatabase.getDao()
    }

    @MainScope
    @Provides
    fun provideFileStorageHelper(context: Context): FilesStorageHelper {
        return FilesStorageHelper(context)
    }

    @MainScope
    @Provides
    @DownloadNotificationHelperImplementation
    fun provideDownloadNotificationHelper(context: Context): NotificationHelper {
        return DownloadNotificationHelper(context)
    }

    @MainScope
    @Provides
    @PlayerNotificationHelperImplementation
    fun providePlayerNotificationHelper(
        context: Context,
        requestManager: RequestManager,
        ui: MainCoroutineScope,
        playbackSession: PlaybackSession
    ): NotificationHelper {
        return PlayerNotificationHelper(context, requestManager, ui, playbackSession)
    }

    @MainScope
    @Provides
    fun provideEventBus(): EventBus {
        return EventBus.getDefault()
    }

    @Module
    interface StaticBindings {

        @MainScope
        @Binds
        fun provideTracksRepository(tracksRepositoryImpl: TracksRepositoryImpl): TracksRepository

        @MainScope
        @Binds
        fun provideNavigationHelper(navigationHelperImpl: NavigationHelperImpl): NavigationHelper
    }
}