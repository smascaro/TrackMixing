package com.smascaro.trackmixing.common.data.datasource.repository

import com.smascaro.trackmixing.common.data.model.DownloadEntity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

class TracksRepositoryImpl @Inject constructor(private val downloadsDao: DownloadsDao) :
    TracksRepository {
    override suspend fun get(videoId: String): DownloadEntity = withContext(Dispatchers.IO) {
        val foundTracks = downloadsDao.get(videoId)
        if (foundTracks.isNotEmpty()) {
            foundTracks.first()
        } else {
            throw TrackNotFoundException("Track with id $videoId is not present in database")
        }
    }

    override suspend fun getAll(): List<DownloadEntity> {
        return downloadsDao.getAll()
    }

    override suspend fun update(entity: DownloadEntity) {
        downloadsDao.update(entity)
    }

    override suspend fun insert(entity: DownloadEntity): Long {
        return downloadsDao.insert(entity)
    }
}
