package com.smascaro.trackmixing.tracks

import android.util.StatsLog
import com.smascaro.trackmixing.common.FilesStorageHelper
import com.smascaro.trackmixing.data.DownloadsDao
import com.smascaro.trackmixing.data.entities.DownloadEntity
import com.smascaro.trackmixing.networking.NodeDownloadsApi
import com.smascaro.trackmixing.networking.availableTracks.AvailableTracksResponseSchema
import com.smascaro.trackmixing.ui.common.BaseObservable
import kotlinx.coroutines.*
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import timber.log.Timber
import java.io.BufferedInputStream
import java.io.File
import java.io.FileInputStream
import java.io.InputStream
import java.lang.Exception
import java.util.*
import java.util.zip.ZipEntry
import java.util.zip.ZipFile
import java.util.zip.ZipInputStream

class DownloadTrackUseCase(
    private val mNodeDownloadsApi: NodeDownloadsApi,
    private val mDao: DownloadsDao,
    private val mFilesStorageHelper: FilesStorageHelper
) :
    BaseObservable<DownloadTrackUseCase.Listener>() {
    interface Listener {
        fun onDownloadTrackStarted(mTrack: Track)
        fun onDownloadTrackFinished(mTrack: Track, path: String)
        fun onDownloadTrackError()
    }

    private var mTrack: Track? = null
    fun downloadTrackAndNotify(track: Track, baseDirectory: String) {
        mTrack = track
        var entity = DownloadEntity(
            0,
            track.videoKey,
            "low",
            track.title,
            track.thumbnailUrl,
            Calendar.getInstance().toString(),
            "",
            DownloadEntity.DownloadStatus.PENDING
        )
        GlobalScope.launch {
            mDao.insert(entity)
            Timber.d("Downloading track with id ${track.videoKey}")
            notifyDownloadStarted(track)
            mNodeDownloadsApi.downloadTrack(entity.sourceVideoKey).enqueue(object :
                Callback<ResponseBody> {
                override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                    Timber.e(t)
                    GlobalScope.launch {
                        setErrorStatusAndUpdate(entity)
                    }
                    notifyError("Error de red en la descarga")
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
                            if (downloadedFilePath.isNotEmpty()) {
                                if (unzipContent(downloadedFilePath)) {
                                    mFilesStorageHelper.deleteFile(downloadedFilePath)
                                    entity.apply {
                                        downloadPath = File(downloadedFilePath).parent ?: ""
                                        status = DownloadEntity.DownloadStatus.FINISHED
                                    }
                                    mDao.update(entity)
                                    notifyDownloadFinished(track, downloadedFilePath)
                                } else {
                                    setErrorStatusAndUpdate(entity)
                                    notifyError("Error al descomprimir el contenido")
                                }
                            } else {
                                setErrorStatusAndUpdate(entity)
                                notifyError("Error en la descarga")
                            }
                        }
                    }
                }


            })
        }

    }

    private suspend fun setErrorStatusAndUpdate(entity: DownloadEntity) {
        entity.apply {
            status = DownloadEntity.DownloadStatus.ERROR
        }
        mDao.update(entity)
    }

    data class ZipIO(val entry: ZipEntry, val output: File)

    private fun unzipContent(pathToZipFile: String): Boolean {
        val zipFile = File(pathToZipFile)
        return try {
            ZipFile(pathToZipFile).use { zip ->
                zip.entries().asSequence().map { entry ->
                    val outputFile = File(zipFile.parent, entry.name)
                    ZipIO(entry, outputFile).also { zipio ->
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

//
//    private fun InputStream.saveToFile(file: String) = use { input ->
//        File(file).outputStream().use { output ->
//            input.copyTo(output)
//        }
//    }
//
//    fun writeFileToStorage(baseDirectory: String, videoId: String, body: ResponseBody): String {
//        Timber.d("Writing to storage download with id $videoId and a length of ${body.contentLength()} bytes")
//        val targetDirectoryFile = File(baseDirectory, videoId)
//        targetDirectoryFile.mkdirs()
//        val targetFile = File(targetDirectoryFile, "$videoId.zip")
//        return try {
//            body.byteStream()?.saveToFile(targetFile.path)
//            targetFile.path
//        } catch (e: Exception) {
//            Timber.e(e)
//            ""
//        }
//
//    }

    private fun notifyDownloadStarted(track: Track) {
        getListeners().forEach { listener ->
            listener.onDownloadTrackStarted(track)
        }
    }

    private fun notifyDownloadFinished(
        track: Track,
        path: String
    ) {
        getListeners().forEach {
            it.onDownloadTrackFinished(track, path)
        }
    }

    private fun notifyError(s: String) {
        getListeners().forEach {
            it.onDownloadTrackError()
        }
    }

}