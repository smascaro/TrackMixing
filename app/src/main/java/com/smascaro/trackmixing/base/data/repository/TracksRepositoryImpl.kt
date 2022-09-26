package com.smascaro.trackmixing.base.data.repository

import com.smascaro.trackmixing.base.data.model.DownloadEntity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

class TracksRepositoryImpl @Inject constructor(private val downloadsDao: DownloadsDao) :
    TracksRepository {
    override suspend fun get(videoId: String): com.smascaro.trackmixing.base.data.model.DownloadEntity = withContext(Dispatchers.IO) {
        val foundTracks = downloadsDao.get(videoId)
        if (foundTracks.isNotEmpty()) {
            foundTracks.first()
        } else {
            throw TrackNotFoundException("Track with id $videoId is not present in database")
        }
    }

    override suspend fun getAll(): List<com.smascaro.trackmixing.base.data.model.DownloadEntity> {
        return downloadsDao.getAll()
    }

    override suspend fun update(entity: com.smascaro.trackmixing.base.data.model.DownloadEntity) {
        downloadsDao.update(entity)
    }

    override suspend fun insert(entity: com.smascaro.trackmixing.base.data.model.DownloadEntity): Long {
        return downloadsDao.insert(entity)
    }

    override suspend fun delete(key: String) {
        downloadsDao.delete(key)
    }
}
