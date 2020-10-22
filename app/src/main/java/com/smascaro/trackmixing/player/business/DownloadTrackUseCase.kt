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
import com.smascaro.trackmixing.player.business.downloadtrack.model.FetchTrackDetailsResponseSchema
import com.smascaro.trackmixing.player.business.downloadtrack.model.toModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import okhttp3.ResponseBody
import org.greenrobot.eventbus.EventBus
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import timber.log.Timber
import java.io.File
import java.util.*
import java.util.zip.ZipEntry
import java.util.zip.ZipFile
import javax.inject.Inject

class DownloadTrackUseCase @Inject constructor(
    val nodeApi: NodeApi,
    val mNodeDownloadsApi: NodeDownloadsApi,
    val mFilesStorageHelper: FilesStorageHelper,
    val tracksRepository: TracksRepository,
    val colorExtractor: ColorExtractor
) :
    BaseObservable<DownloadTrackUseCase.Listener>() {
    interface Listener {
        fun onDownloadTrackStarted(mTrack: Track)
        fun onDownloadTrackFinished(track: Track, path: String)
        fun onDownloadTrackError()
    }

    private var mTrack: Track? = null

    fun execute(videoId: String) {
        EventBus.getDefault().post(
            DownloadEvents.ProgressUpdate(
                "",
                5,
                "Fetching details",
                FetchSteps.DownloadStep()
            )
        )
        nodeApi.fetchDetails(videoId).enqueue(object : Callback<FetchTrackDetailsResponseSchema> {
            override fun onResponse(
                call: Call<FetchTrackDetailsResponseSchema>,
                response: Response<FetchTrackDetailsResponseSchema>
            ) {
                val responseBody = response.body()
                if (responseBody != null) {
                    mTrack = responseBody.toModel()
                    downloadTrackAndNotify(mFilesStorageHelper.getBaseDirectory())
                }
            }

            override fun onFailure(call: Call<FetchTrackDetailsResponseSchema>, t: Throwable) {
                notifyError()
            }
        })
    }

    private fun downloadTrackAndNotify(baseDirectory: String) {
        if (mTrack != null) {
            GlobalScope.launch {
                val trackFromDatabase = try {
                    tracksRepository.get(mTrack!!.videoKey)
                } catch (tnfe: TrackNotFoundException) {
                    null
                }
                val filesExist = if (trackFromDatabase != null) {
                    mFilesStorageHelper.checkContent(trackFromDatabase.downloadPath)
                } else {
                    false
                }
                if (trackFromDatabase == null || !filesExist) {
                    this@DownloadTrackUseCase.mTrack = mTrack
                    var entity =
                        DownloadEntity(
                            0,
                            mTrack!!.videoKey,
                            "low",
                            mTrack!!.title,
                            mTrack!!.author,
                            mTrack!!.thumbnailUrl,
                            Calendar.getInstance().toString(),
                            mFilesStorageHelper.getBaseDirectoryByVideoId(mTrack!!.videoKey),
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
                    mNodeDownloadsApi.downloadTrack(entity.sourceVideoKey).enqueue(object :
                        Callback<ResponseBody> {
                        override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                            Timber.e(t)
                            GlobalScope.launch {
                                setErrorStatusAndUpdate(entity)
                            }
                            notifyError()
                        }

                        override fun onResponse(
                            call: Call<ResponseBody>,
                            response: Response<ResponseBody>
                        ) {
                            val responseBody = response.body()
                            if (responseBody != null) {
                                GlobalScope.launch {
                                    val downloadedFilePath =
                                        mFilesStorageHelper.writeFileToStorage(
                                            baseDirectory,
                                            entity.sourceVideoKey,
                                            responseBody
                                        )
                                    EventBus.getDefault().post(
                                        DownloadEvents.ProgressUpdate(
                                            mTrack!!.title,
                                            80,
                                            "Unzipping contents",
                                            FetchSteps.DownloadStep()
                                        )
                                    )
                                    if (downloadedFilePath.isNotEmpty()) {
                                        val downloadedBasePath =
                                            File(downloadedFilePath).parent ?: ""
                                        if (unzipContent(downloadedFilePath)) {
                                            EventBus.getDefault().post(
                                                DownloadEvents.ProgressUpdate(
                                                    mTrack!!.title,
                                                    95,
                                                    "Cleaning unneeded files",
                                                    FetchSteps.DownloadStep()
                                                )
                                            )
                                            mFilesStorageHelper.deleteFile(downloadedFilePath)
                                            entity.apply {
                                                status = DownloadEntity.DownloadStatus.FINISHED
                                            }
                                            colorExtractor.extractLightVibrant(entity.thumbnailUrl) {
                                                entity.backgroundColor = it
                                                CoroutineScope(Dispatchers.IO).launch {
                                                    tracksRepository.update(entity)
                                                    Timber.d("Updated entity -> $entity")
                                                }
                                            }
                                            notifyDownloadFinished(
                                                entity.toModel(),
                                                downloadedBasePath
                                            )
                                        } else {
                                            setErrorStatusAndUpdate(entity)
                                            notifyError()
                                        }
                                    } else {
                                        setErrorStatusAndUpdate(entity)
                                        notifyError()
                                    }
                                }
                            }
                        }
                    })
                } else {
                    Timber.i("Track ${mTrack!!.videoKey} already in the system, omitting download")
                    notifyDownloadFinished(mTrack!!, trackFromDatabase.downloadPath)
                }
            }
        }
    }

    private suspend fun setErrorStatusAndUpdate(entity: DownloadEntity) {
        entity.apply {
            status = DownloadEntity.DownloadStatus.ERROR
        }
        tracksRepository.update(entity)
    }

    data class ZipIO(val entry: ZipEntry, val output: File)

    private fun unzipContent(pathToZipFile: String): Boolean {
        val zipFile = File(pathToZipFile)
        return try {
            ZipFile(pathToZipFile).use { zip ->
                zip.entries().asSequence().map { entry ->
                    val outputFile = File(zipFile.parent, entry.name)
                    ZipIO(
                        entry,
                        outputFile
                    ).also { zipio ->
                        zipio.output.parentFile?.run {
                            if (!exists()) {
                                mkdirs()
                            }
                        }
                    }
                }.filter {
                    !it.entry.isDirectory
                }.forEach { (entry, output) ->
                    zip.getInputStream(entry).use { input ->
                        output.outputStream().use { output ->
                            input.copyTo(output)
                        }
                    }
                }
            }
            true
        } catch (e: Exception) {
            Timber.e(e)
            false
        }
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

    private fun notifyError() {
        getListeners().forEach {
            it.onDownloadTrackError()
        }
    }
}