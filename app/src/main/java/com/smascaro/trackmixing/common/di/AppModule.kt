package com.smascaro.trackmixing.common.di

import android.content.Context
import com.bumptech.glide.RequestManager
import com.smascaro.trackmixing.base.coroutine.MainCoroutineScope
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

@Module
class AppModule {

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

    @Module
    interface StaticBindings {

        @MainScope
        @Binds
        fun provideNavigationHelper(navigationHelperImpl: NavigationHelperImpl): NavigationHelper
    }
}