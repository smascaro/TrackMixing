package com.smascaro.trackmixing.common.data.datasource.repository

import com.smascaro.trackmixing.common.data.model.DownloadEntity

interface TracksRepository {
    suspend fun get(videoId: String): DownloadEntity
    suspend fun getAll(): List<DownloadEntity>
    suspend fun update(entity: DownloadEntity)
    suspend fun insert(entity: DownloadEntity): Long
}