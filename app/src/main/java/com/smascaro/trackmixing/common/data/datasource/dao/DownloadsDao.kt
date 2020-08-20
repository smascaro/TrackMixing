package com.smascaro.trackmixing.common.data.datasource.dao

import androidx.room.*
import com.smascaro.trackmixing.common.data.model.DownloadEntity

@Dao
interface DownloadsDao {
    @Query("SELECT * FROM download")
    fun getAll(): List<DownloadEntity>

    @Query("SELECT * FROM download WHERE sourceVideoKey = :videoKey")
    fun get(videoKey: String): List<DownloadEntity>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(downloadEntity: DownloadEntity): Long

    @Update
    suspend fun update(downloadEntity: DownloadEntity)
}