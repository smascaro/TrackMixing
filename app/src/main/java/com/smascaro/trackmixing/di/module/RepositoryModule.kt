package com.smascaro.trackmixing.di.module

import android.content.Context
import com.smascaro.trackmixing.base.data.repository.DownloadsDao
import com.smascaro.trackmixing.base.data.repository.DownloadsDatabase
import com.smascaro.trackmixing.base.data.repository.TracksRepository
import com.smascaro.trackmixing.base.data.repository.TracksRepositoryImpl
import dagger.Binds
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@Module
class RepositoryModule {
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

    @Module
    interface Bindings {
        @Singleton
        @Binds
        fun provideTracksRepository(tracksRepositoryImpl: TracksRepositoryImpl): TracksRepository
    }
}