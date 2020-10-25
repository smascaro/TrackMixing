package com.smascaro.trackmixing.player.business

import com.smascaro.trackmixing.common.data.datasource.network.NodeApi
import com.smascaro.trackmixing.common.data.datasource.network.NodeDownloadsApi
import com.smascaro.trackmixing.common.data.datasource.repository.TrackNotFoundException
import com.smascaro.trackmixing.common.data.datasource.repository.TracksRepository
import com.smascaro.trackmixing.common.data.datasource.repository.toModel
import com.smascaro.trackmixing.common.data.model.DownloadEntity
import com.smascaro.trackmixing.common.data.model.Track
import com.smascaro.trackmixing.common.utils.FilesStorageHelper
import com.smascaro.trackmixing.common.utils.ui.ColorExtractor
import com.smascaro.trackmixing.common.view.architecture.BaseObservable
import com.smascaro.trackmixing.player.business.downloadtrack.model.DownloadEvents
import com.smascaro.trackmixing.player.business.downloadtrack.model.FetchSteps
import com.smascaro.trackmixing.player.business.downloadtrack.model.toModel
import org.greenrobot.eventbus.EventBus
import timber.log.Timber
import java.io.File
import java.util.*
import javax.inject.Inject

class DownloadTrackUseCase @Inject constructor(
    private val nodeApi: NodeApi,
    private val nodeDownloadsApi: NodeDownloadsApi,
    private val filesStorageHelper: FilesStorageHelper,
    private val tracksRepository: TracksRepository,
    private val colorExtractor: ColorExtractor
) :
    BaseObservable<DownloadTrackUseCase.Listener>() {
    interface Listener {
        fun onDownloadTrackStarted(mTrack: Track)
        fun onDownloadTrackFinished(track: Track, path: String)
        fun onDownloadTrackError()
    }

    private var mTrack: Track? = null

    suspend fun execute(videoId: String) =
        executeInternal(videoId)

    private suspend fun executeInternal(videoId: String) {
        EventBus.getDefault().post(
            DownloadEvents.ProgressUpdate(
                "",
                5,
                "Fetching details",
                FetchSteps.DownloadStep()
            )
        )
        try {
            val detailsResponse = nodeApi.fetchDetails(videoId)
            Timber.i("Details fetched: $detailsResponse")
            mTrack = detailsResponse.toModel()
            downloadTrackAndNotify(filesStorageHelper.getBaseDirectory())
        } catch (e: Exception) {
            notifyError(e)
        }
    }

    private suspend fun downloadTrackAndNotify(baseDirectory: String) {
        if (mTrack == null) return
        val trackFromDatabase = try {
            tracksRepository.get(mTrack!!.videoKey)
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
            var entity =
                DownloadEntity(
                    0,
                    mTrack!!.videoKey,
                    "low",
                    mTrack!!.title,
                    mTrack!!.author,
                    mTrack!!.thumbnailUrl,
                    Calendar.getInstance().toString(),
                    filesStorageHelper.getBaseDirectoryByVideoId(mTrack!!.videoKey),
                    DownloadEntity.DownloadStatus.PENDING,
                    mTrack!!.secondsLong,
                    mTrack!!.backgroundColor
                )
            entity.id = tracksRepository.insert(entity).toInt()
            Timber.d("Downloading track with id ${mTrack!!.videoKey}")
            notifyDownloadStarted(mTrack!!)
            EventBus.getDefault().post(
                DownloadEvents.ProgressUpdate(
                    mTrack!!.title,
                    30,
                    "Downloading files",
                    FetchSteps.DownloadStep()
                )
            )
            try {
                val downloadResponse =
                    nodeDownloadsApi.downloadTrack(entity.sourceVideoKey)
                val downloadedFilePath =
                    filesStorageHelper.writeFileToStorage(
                        baseDirectory,
                        entity.sourceVideoKey,
                        downloadResponse //TODO passar bytestream enlloc de responsebody
                    )
                EventBus.getDefault().post(
                    DownloadEvents.ProgressUpdate(
                        mTrack!!.title,
                        80,
                        "Unzipping contents",
                        FetchSteps.DownloadStep()
                    )
                )
                val downloadedBasePath =
                    File(downloadedFilePath).parent
                        ?: throw Exception("Downloaded file path has no parent")
                filesStorageHelper.unzipContent(downloadedFilePath)
                EventBus.getDefault().post(
                    DownloadEvents.ProgressUpdate(
                        mTrack!!.title,
                        95,
                        "Cleaning unneeded files",
                        FetchSteps.DownloadStep()
                    )
                )
                filesStorageHelper.deleteFile(downloadedFilePath)
                entity.status = DownloadEntity.DownloadStatus.FINISHED
                val extractedColor = colorExtractor.extractLightVibrant(entity.thumbnailUrl)
                entity.backgroundColor = extractedColor
                tracksRepository.update(entity)
                Timber.d("Updated entity -> $entity")
                notifyDownloadFinished(
                    entity.toModel(),
                    downloadedBasePath
                )
            } catch (e: Exception) {
                notifyError(e)
                Timber.e(e)
                //If an error happens, any downloaded data is deleted and track is deleted from db
                rollbackDownload(entity)
            }
        } else {
            Timber.i("Track ${mTrack!!.videoKey} already in the system, omitting download")
            notifyDownloadFinished(mTrack!!, trackFromDatabase.downloadPath)
        }
    }

    private suspend fun rollbackDownload(entity: DownloadEntity) {
        filesStorageHelper.deleteData(entity.downloadPath)
        tracksRepository.delete(entity.sourceVideoKey)
    }

    private fun notifyDownloadStarted(track: Track) {
        getListeners().forEach { listener ->
            listener.onDownloadTrackStarted(track)
        }
    }

    private fun notifyDownloadFinished(
        track: Track,
        path: String
    ) {
        EventBus.getDefault().post(DownloadEvents.FinishedDownloading())
        getListeners().forEach {
            it.onDownloadTrackFinished(track, path)
        }
    }

    private fun notifyError(e: Exception) {
        getListeners().forEach {
            it.onDownloadTrackError()
        }
    }
}