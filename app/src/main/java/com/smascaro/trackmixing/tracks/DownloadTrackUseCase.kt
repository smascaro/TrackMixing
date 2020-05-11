package com.smascaro.trackmixing.tracks

import com.smascaro.trackmixing.data.DownloadsDao
import com.smascaro.trackmixing.data.entities.DownloadEntity
import com.smascaro.trackmixing.networking.NodeApi
import com.smascaro.trackmixing.ui.common.BaseObservableViewMvc
import kotlinx.coroutines.*
import timber.log.Timber
import java.util.*

class DownloadTrackUseCase(private val mNodeApi: NodeApi, private val mDao: DownloadsDao) :
    BaseObservableViewMvc<DownloadTrackUseCase.Listener>() {
    interface Listener {
        fun onDownloadTrackStarted(mTrack: Track)
        fun onDownloadTrackFinished(mTrack: Track)
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
            getListeners().forEach { listener ->
                listener.onDownloadTrackStarted(track)
            }
            mDao.insert(entity)
            delay(5 * 1000)
            entity.status = DownloadEntity.DownloadStatus.FINISHED
            mDao.update(entity)
            getListeners().forEach { listener ->
                listener.onDownloadTrackFinished(track)
            }
        }
    }
}