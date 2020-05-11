package com.smascaro.trackmixing.data

import androidx.room.*
import com.smascaro.trackmixing.data.entities.DownloadEntity

@Dao
interface DownloadsDao {
    @Query("SELECT * FROM download")
    fun getAll(): List<DownloadEntity>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(downloadEntity: DownloadEntity)

    @Update
    suspend fun update(downloadEntity: DownloadEntity)
}