package com.smascaro.trackmixing.base.download

import com.smascaro.trackmixing.base.coroutine.IoCoroutineScope
import com.smascaro.trackmixing.base.data.model.DownloadEntity
import com.smascaro.trackmixing.base.data.repository.TrackNotFoundException
import com.smascaro.trackmixing.base.data.repository.TracksRepository
import com.smascaro.trackmixing.base.ui.architecture.view.BaseObservable
import com.smascaro.trackmixing.base.utils.ColorExtractor
import com.smascaro.trackmixing.base.utils.FilesStorageHelper
import kotlinx.coroutines.launch
import timber.log.Timber
import java.io.InputStream
import javax.inject.Inject

class TrackDownloader @Inject constructor(
    private val filesStorageHelper: FilesStorageHelper,
    private val tracksRepository: TracksRepository,
    private val colorExtractor: ColorExtractor,
    private val io: com.smascaro.trackmixing.base.coroutine.IoCoroutineScope
) : BaseObservable<TrackDownloader.Listener>() {
    interface Listener {
        fun onProgressFeedback(videoId: String, progress: Int, message: String)
        fun onDownloadFinished(videoId: String)
        fun onDownloadError(videoId: String, e: Exception)
    }

    data class EntityData(
        val videoId: String,
        val quality: String = "low",
        val title: String,
        val author: String,
        val thumbnailUrl: String,
        val date: String,
        val secondsLong: Int
    )

    fun startDownload(stream: InputStream, entity: EntityData) = io.launch {
        try {
            val trackFromDatabase = try {
                tracksRepository.get(entity.videoId)
            } catch (tnfe: TrackNotFoundException) {
                null
            }
            val filesExist = if (trackFromDatabase != null) {
                filesStorageHelper.checkContent(trackFromDatabase.downloadPath)
            } else {
                false
            }
            if (trackFromDatabase != null && !filesExist) {
                //if track is in database but there are no files, inconsistency => delete from db
                tracksRepository.delete(trackFromDatabase.sourceVideoKey)
            }
            if (trackFromDatabase == null || !filesExist) {
                val dbEntity =
                    com.smascaro.trackmixing.base.data.model.DownloadEntity(
                        0,
                        entity.videoId,
                        entity.quality,
                        entity.title,
                        entity.author,
                        entity.thumbnailUrl,
                        System.currentTimeMillis(),
                        filesStorageHelper.getBaseDirectoryByVideoId(entity.videoId),
                        com.smascaro.trackmixing.base.data.model.DownloadEntity.DownloadStatus.PENDING,
                        entity.secondsLong
                    )
                dbEntity.id = tracksRepository.insert(dbEntity).toInt()
                Timber.d("Downloading track with id ${entity.videoId}")
                notifyProgress(entity, 30, "Downloading files")
                val downloadedFilePath =
                    filesStorageHelper.writeFileToStorage(
                        filesStorageHelper.getBaseDirectory(),
                        entity.videoId,
                        stream
                    )
                notifyProgress(entity, 80, "Unzipping contents")
                filesStorageHelper.unzipContent(downloadedFilePath)
                notifyProgress(entity, 95, "Cleaning unneeded files")
                filesStorageHelper.deleteFile(downloadedFilePath)
                dbEntity.status = com.smascaro.trackmixing.base.data.model.DownloadEntity.DownloadStatus.FINISHED
                dbEntity.backgroundColor =
                    colorExtractor.extractLightVibrant(dbEntity.thumbnailUrl)
                tracksRepository.update(dbEntity)
                Timber.d("Updated entity -> $dbEntity")
                notifyDownloadFinished(entity)
            }
        } catch (e: Exception) {
            rollbackDownload(entity.videoId)
            notifyDownloadError(entity, e)
        }
    }

    private fun notifyDownloadError(
        entity: EntityData,
        e: Exception
    ) {
        getListeners().forEach { it.onDownloadError(entity.videoId, e) }
    }

    private fun notifyDownloadFinished(entity: EntityData) {
        getListeners().forEach {
            it.onDownloadFinished(entity.videoId)
        }
    }

    private fun notifyProgress(entity: EntityData, progress: Int, message: String) {
        getListeners().forEach {
            it.onProgressFeedback(entity.videoId, progress, message)
        }
    }

    private suspend fun rollbackDownload(videoId: String) {
        filesStorageHelper.deleteData(filesStorageHelper.getBaseDirectoryByVideoId(videoId))
        tracksRepository.delete(videoId)
    }
}