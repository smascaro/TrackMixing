package com.smascaro.trackmixing.settings.business.downloadtestdata.usecase

import com.smascaro.trackmixing.base.coroutine.IoCoroutineScope
import com.smascaro.trackmixing.base.coroutine.MainCoroutineScope
import com.smascaro.trackmixing.base.data.repository.TracksRepository
import com.smascaro.trackmixing.base.network.testdata.api.AwsContract
import com.smascaro.trackmixing.base.network.testdata.api.TestDataApi
import com.smascaro.trackmixing.base.network.testdata.api.TestDataFilesApi
import com.smascaro.trackmixing.base.time.TimeHelper
import com.smascaro.trackmixing.common.data.datasource.TrackDownloader
import com.smascaro.trackmixing.common.utils.FilesStorageHelper
import com.smascaro.trackmixing.common.view.architecture.BaseObservable
import com.smascaro.trackmixing.main.components.progress.model.UiProgressEvent
import com.smascaro.trackmixing.settings.business.downloadtestdata.selection.model.TestDataBundleInfo
import com.smascaro.trackmixing.settings.business.downloadtestdata.selection.model.toModelList
import kotlinx.coroutines.launch
import org.greenrobot.eventbus.EventBus
import timber.log.Timber
import java.util.*
import java.util.concurrent.atomic.AtomicBoolean
import javax.inject.Inject

class DownloadTestDataUseCase @Inject constructor(
    private val testDataApi: TestDataApi,
    private val testDataFilesApi: TestDataFilesApi,
    private val filesStorageHelper: FilesStorageHelper,
    private val tracksRepository: TracksRepository,
    private val trackDownloader: TrackDownloader,
    private val ui: MainCoroutineScope,
    private val io: IoCoroutineScope,
    private val eventBus: EventBus
) : BaseObservable<DownloadTestDataUseCase.Listener>(), TrackDownloader.Listener {
    sealed class Result {
        class Success(val tracks: List<TestDataBundleInfo>) : Result()
        class Failure(val throwable: Throwable) : Result()
    }

    interface Listener {
        fun onFinishedItemDownload(videoKey: String)
        fun onItemDownloadFailed(trackId: String, throwable: Throwable)
    }

    private val cancellationFlag: AtomicBoolean = AtomicBoolean(false)
    suspend fun getTestDataBundleInfo(): Result {
        return try {
            val response =
                testDataApi.downloadTestDataBundleFile(AwsContract.INFO_FILE_RESOURCE)
            Timber.i(response.toString())
            Result.Success(response.toModelList())
        } catch (e: Exception) {
            Result.Failure(e)
        }
    }

    fun downloadItemBundle(bundleInfo: TestDataBundleInfo) {
        io.launch {
            val downloadResponse =
                testDataFilesApi.downloadTestItemBundle(bundleInfo.resourceFilename)
            trackDownloader.registerListener(this@DownloadTestDataUseCase)
            val data = TrackDownloader.EntityData(
                bundleInfo.videoKey,
                title = bundleInfo.title,
                author = bundleInfo.author,
                thumbnailUrl = bundleInfo.thumbnailUrl,
                secondsLong = TimeHelper.fromString(bundleInfo.duration).toSeconds(),
                date = Calendar.getInstance().toString()
            )
            trackDownloader.startDownload(downloadResponse.byteStream(), data)
        }
    }

    private fun notifyDownloadFinished(videoId: String) =
        ui.launch {
            if (!cancellationFlag.get()) {
                //If it is marked for cancellation, do not post Ui event
                eventBus.post(UiProgressEvent.ProgressFinished())
            }
            getListeners().forEach {
                it.onFinishedItemDownload(videoId)
            }
        }

    private fun notifyDownloadError(videoId: String, e: Throwable) =
        ui.launch {
            getListeners().forEach {
                it.onItemDownloadFailed(videoId, e)
            }
        }

    fun rollbackDownloads(items: List<TestDataBundleInfo>) {
        Timber.d("Rolling back ${items.size} items")
        items.forEach {
            Timber.d("Deleting files for item ${it.videoKey}")
            filesStorageHelper.deleteData(filesStorageHelper.getBaseDirectoryByVideoId(it.videoKey))
            io.launch {
                Timber.d("Deleting database registry for item ${it.videoKey}")
                tracksRepository.delete(it.videoKey)
            }
        }
    }

    fun markForCancellation() {
        cancellationFlag.set(true)
    }

    override fun onProgressFeedback(videoId: String, progress: Int, message: String) {
        Timber.d("Download progress update: $videoId at $progress%: $message")
    }

    override fun onDownloadFinished(videoId: String) {
        trackDownloader.unregisterListener(this)
        notifyDownloadFinished(videoId)
    }

    override fun onDownloadError(videoId: String, e: Exception) {
        trackDownloader.unregisterListener(this)
        notifyDownloadError(videoId, e)
    }
}