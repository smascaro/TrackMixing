package com.smascaro.trackmixing.base.data.repository

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.smascaro.trackmixing.base.data.model.DownloadEntity

@Database(entities = [com.smascaro.trackmixing.base.data.model.DownloadEntity::class], version = 7, exportSchema = false)
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