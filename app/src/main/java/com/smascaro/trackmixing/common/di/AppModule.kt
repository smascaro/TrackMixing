package com.smascaro.trackmixing.common.di

import android.content.Context
import com.bumptech.glide.Glide
import com.bumptech.glide.RequestManager
import com.smascaro.trackmixing.common.utils.FilesStorageHelper
import com.smascaro.trackmixing.common.data.datasource.dao.DownloadsDao
import com.smascaro.trackmixing.common.data.datasource.dao.DownloadsDatabase
import com.smascaro.trackmixing.playbackservice.utils.PlaybackSession
import com.smascaro.trackmixing.playbackservice.utils.PlaybackSessionImpl
import dagger.Binds
import dagger.Module
import dagger.Provides
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

    @Module
    interface StaticBindings {
        @Singleton
        @Binds
        fun providePlaybackSession(playbackSessionImpl: PlaybackSessionImpl): PlaybackSession
    }
}