package com.smascaro.trackmixing.tracks

import android.content.Context
import com.smascaro.trackmixing.data.DownloadsDao
import com.smascaro.trackmixing.data.entities.DownloadEntity
import com.smascaro.trackmixing.networking.NodeDownloadsApi
import com.smascaro.trackmixing.ui.common.BaseObservableViewMvc
import kotlinx.coroutines.*
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import timber.log.Timber
import java.io.File
import java.io.InputStream
import java.lang.Exception
import java.util.*

class DownloadTrackUseCase(
    private val mNodeDownloadsApi: NodeDownloadsApi,
    private val mDao: DownloadsDao,
    private val mContext: Context
) :
    BaseObservableViewMvc<DownloadTrackUseCase.Listener>() {
    interface Listener {
        fun onDownloadTrackStarted(mTrack: Track)
        fun onDownloadTrackFinished(mTrack: Track, path: String)
        fun onDownloadTrackError()
    }

    private var mTrack: Track? = null
    fun downloadTrackAndNotify(track: Track) {
        mTrack = track
        Timber.d("We currently simulate the download api call")
        var entity = DownloadEntity(
            0,
            track.videoKey,
            "low",
            track.title,
            track.thumbnailUrl,
            Calendar.getInstance().toString(),
            "tempPath/${track.videoKey}",
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
                                writeFileToStorage(entity.sourceVideoKey, responseBody)
                            if (downloadedFilePath.isNotEmpty()) {
                                notifyDownloadFinished(track, downloadedFilePath)
                            } else {
                                notifyError()
                            }
                        }
                    }
                }


            })
            entity.status = DownloadEntity.DownloadStatus.FINISHED
            mDao.update(entity)
        }

    }

    private fun InputStream.saveToFile(file: String) = use { input ->
        File(file).outputStream().use { output ->
            input.copyTo(output)
        }
    }

    fun writeFileToStorage(videoId: String, body: ResponseBody): String {
        Timber.d("Writing to storage download with id $videoId and a length of ${body.contentLength()} bytes")
        val targetDirectoryFile = File(mContext.filesDir?.path, videoId)
        targetDirectoryFile.mkdirs()
        val targetFile = File(targetDirectoryFile, "$videoId.zip")
        return try {
            body.byteStream()?.saveToFile(targetFile.path)
            targetFile.path
        } catch (e: Exception) {
            Timber.e(e)
            ""
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