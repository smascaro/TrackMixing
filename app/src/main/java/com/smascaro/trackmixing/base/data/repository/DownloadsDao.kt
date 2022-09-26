package com.smascaro.trackmixing.base.data.repository

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.smascaro.trackmixing.base.data.model.DownloadEntity

@Dao
interface DownloadsDao {
    @Query("SELECT * FROM download")
    fun getAll(): List<com.smascaro.trackmixing.base.data.model.DownloadEntity>

    @Query("SELECT * FROM download WHERE sourceVideoKey = :videoKey")
    fun get(videoKey: String): List<com.smascaro.trackmixing.base.data.model.DownloadEntity>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(downloadEntity: com.smascaro.trackmixing.base.data.model.DownloadEntity): Long

    @Update
    suspend fun update(downloadEntity: com.smascaro.trackmixing.base.data.model.DownloadEntity)

    @Query("DELETE FROM DOWNLOAD WHERE sourceVideoKey=:key")
    suspend fun delete(key: String)
}