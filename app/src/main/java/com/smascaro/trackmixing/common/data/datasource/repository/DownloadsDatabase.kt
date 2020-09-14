package com.smascaro.trackmixing.common.data.datasource.repository

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.smascaro.trackmixing.common.data.model.DownloadEntity

@Database(entities = [DownloadEntity::class], version = 6, exportSchema = false)
abstract class DownloadsDatabase : RoomDatabase() {
    abstract fun getDao(): DownloadsDao

    companion object {
        @Volatile
        private var INSTANCE: DownloadsDatabase? = null

        fun getDatabase(context: Context): DownloadsDatabase {
            val tempInstance =
                INSTANCE
            if (tempInstance != null) {
                return tempInstance
            }
            synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    DownloadsDatabase::class.java,
                    "downloads_database"
                ).fallbackToDestructiveMigration()//TODO: Change for actual migration scripts
                    .build()
                INSTANCE = instance
                return instance
            }
        }
    }
}