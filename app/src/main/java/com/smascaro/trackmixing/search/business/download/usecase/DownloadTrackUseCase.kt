package com.smascaro.trackmixing.search.business.download.usecase

import com.smascaro.trackmixing.base.coroutine.MainCoroutineScope
import com.smascaro.trackmixing.base.data.model.Track
import com.smascaro.trackmixing.base.network.node.api.NodeApi
import com.smascaro.trackmixing.base.network.node.api.NodeDownloadsApi
import com.smascaro.trackmixing.base.network.youtube.model.toModel
import com.smascaro.trackmixing.common.data.datasource.TrackDownloader
import com.smascaro.trackmixing.search.business.download.model.DownloadEvents
import com.smascaro.trackmixing.search.business.download.model.FetchSteps
import kotlinx.coroutines.launch
import org.greenrobot.eventbus.EventBus
import timber.log.Timber
import java.util.*
import javax.inject.Inject

class DownloadTrackUseCase @Inject constructor(
    private val nodeApi: NodeApi,
    private val nodeDownloadsApi: NodeDownloadsApi,
    private val trackDownloader: TrackDownloader,
    private val eventBus: EventBus,
    private val ui: MainCoroutineScope
) : TrackDownloader.Listener {
    private var currentTrack: Track? = null

    suspend fun execute(videoId: String) =
        executeInternal(videoId)

    private suspend fun executeInternal(videoId: String) {
        postProgressEvent(5, "Fetching details")
        try {
            val detailsResponse = nodeApi.fetchDetails(videoId)
            Timber.i("Details fetched: $detailsResponse")
            val track = detailsResponse.toModel()
            currentTrack = track
            downloadTrackAndNotify(track)
        } catch (e: Exception) {
            onDownloadError(videoId, e)
        }
    }

    private suspend fun downloadTrackAndNotify(track: Track) {
        try {
            val downloadResponse =
                nodeDownloadsApi.downloadTrack(track.videoKey)
            trackDownloader.registerListener(this)
            val data = TrackDownloader.EntityData(
                track.videoKey,
                title = track.title,
                author = track.author,
                date = Calendar.getInstance().toString(),
                thumbnailUrl = track.thumbnailUrl,
                secondsLong = track.secondsLong.value.toInt()
            )
            trackDownloader.startDownload(downloadResponse.byteStream(), data)
        } catch (e: Exception) {
            Timber.e(e)
        }
    }

    override fun onProgressFeedback(videoId: String, progress: Int, message: String) {
        postProgressEvent(progress, message)
    }

    private fun postProgressEvent(progress: Int, message: String) = ui.launch {
        eventBus.post(
            DownloadEvents.ProgressUpdate(
                currentTrack?.title ?: "Fetching track title...",
                progress,
                message,
                FetchSteps.DownloadStep()
            )
        )
    }

    override fun onDownloadFinished(videoId: String) {
        Timber.i("Track $videoId finished downloading")
        trackDownloader.unregisterListener(this)
        eventBus.post(DownloadEvents.FinishedDownloading())
    }

    override fun onDownloadError(videoId: String, e: Exception) {
        Timber.e("Error downloading Track $videoId: $e")
        trackDownloader.unregisterListener(this)
        eventBus.post(DownloadEvents.ErrorOccurred(e.localizedMessage ?: "Unknown error"))
    }
}