package com.smascaro.trackmixing.settings.business.downloadtestdata.usecase

import com.smascaro.trackmixing.common.data.datasource.repository.TracksRepository
import com.smascaro.trackmixing.common.data.model.DownloadEntity
import com.smascaro.trackmixing.common.utils.AWS_S3_TEST_DATA_INFO_FILE_RESOURCE
import com.smascaro.trackmixing.common.utils.FilesStorageHelper
import com.smascaro.trackmixing.common.utils.TimeHelper
import com.smascaro.trackmixing.common.utils.ui.ColorExtractor
import com.smascaro.trackmixing.common.view.architecture.BaseObservable
import com.smascaro.trackmixing.main.components.progress.model.UiProgressEvent
import com.smascaro.trackmixing.settings.business.downloadtestdata.selection.model.TestDataBundleInfo
import com.smascaro.trackmixing.settings.business.downloadtestdata.selection.model.TestDataBundleInfoResponseSchema
import com.smascaro.trackmixing.settings.business.downloadtestdata.selection.model.toModelList
import com.smascaro.trackmixing.settings.business.downloadtestdata.usecase.data.TestDataApi
import com.smascaro.trackmixing.settings.business.downloadtestdata.usecase.data.TestDataFilesApi
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import okhttp3.ResponseBody
import org.greenrobot.eventbus.EventBus
import retrofit2.Call
import retrofit2.Response
import timber.log.Timber
import java.io.File
import java.io.IOException
import java.util.*
import java.util.concurrent.atomic.AtomicBoolean
import javax.inject.Inject

class DownloadTestDataUseCase @Inject constructor(
    private val testDataApi: TestDataApi,
    private val testDataFilesApi: TestDataFilesApi,
    private val filesStorageHelper: FilesStorageHelper,
    private val tracksRepository: TracksRepository,
    private val colorExtractor: ColorExtractor
) : BaseObservable<DownloadTestDataUseCase.Listener>() {
    sealed class Result {
        class Success(val tracks: List<TestDataBundleInfo>) : Result()
        class Failure(val throwable: Throwable) : Result()
    }

    interface Listener {
        fun onFinishedItemDownload(videoKey: String)
        fun onItemDownloadFailed(item: TestDataBundleInfo, throwable: Throwable)
    }

    private val cancellationFlag: AtomicBoolean = AtomicBoolean(false)
    fun getTestDataBundleInfo(callback: (Result) -> Unit) {
        testDataApi.downloadTestDataBundleFile(AWS_S3_TEST_DATA_INFO_FILE_RESOURCE)
            .enqueue(object : retrofit2.Callback<TestDataBundleInfoResponseSchema> {
                override fun onFailure(
                    call: Call<TestDataBundleInfoResponseSchema>,
                    t: Throwable
                ) {
                    callback(Result.Failure(t))
                }

                override fun onResponse(
                    call: Call<TestDataBundleInfoResponseSchema>,
                    response: Response<TestDataBundleInfoResponseSchema>
                ) {
                    if (!response.isSuccessful) {
                        throw IOException("Unexpected code $response")
                    }
                    response.body()?.let { body ->
                        Timber.d(body.toString())
                        val tracksList = body.toModelList()
                        callback(Result.Success(tracksList))
                    }
                }
            })
    }

    fun downloadItemBundle(bundleInfo: TestDataBundleInfo) {
        testDataFilesApi.downloadTestItemBundle(bundleInfo.resourceFilename)
            .enqueue(object : retrofit2.Callback<ResponseBody> {
                override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                    notifyDownloadError(bundleInfo, t)
                }

                override fun onResponse(
                    call: Call<ResponseBody>,
                    response: Response<ResponseBody>
                ) {
                    val body = response.body()
                    if (response.isSuccessful && body != null) {
                        CoroutineScope(Dispatchers.IO).launch {
                            try {
                                val downloadPath =
                                    filesStorageHelper.getBaseDirectoryByVideoId(bundleInfo.videoKey)
                                val baseDirectory = filesStorageHelper.getBaseDirectory()
                                val entity = DownloadEntity(
                                    0,
                                    bundleInfo.videoKey,
                                    "low",
                                    bundleInfo.title,
                                    bundleInfo.author,
                                    bundleInfo.thumbnailUrl,
                                    Calendar.getInstance().toString(),
                                    downloadPath,
                                    DownloadEntity.DownloadStatus.PENDING,
                                    TimeHelper.fromString(bundleInfo.duration).toSeconds()
                                )
                                entity.id = tracksRepository.insert(entity).toInt()
                                val downloadFilePath =
                                    filesStorageHelper.writeFileToStorage(
                                        baseDirectory,
                                        entity.sourceVideoKey,
                                        body
                                    )
                                if (downloadFilePath.isNotEmpty()) {
                                    val downloadParentPath =
                                        File(downloadFilePath).parent ?: ""
                                    if (filesStorageHelper.unzipContent(downloadFilePath)) {
                                        filesStorageHelper.deleteFile(downloadFilePath)
                                        entity.status =
                                            DownloadEntity.DownloadStatus.FINISHED
                                        colorExtractor.extractLightVibrant(entity.thumbnailUrl) {
                                            entity.backgroundColor = it
                                            CoroutineScope(Dispatchers.IO).launch {
                                                tracksRepository.update(entity)
                                                Timber.d("Updated entity -> $entity")
                                            }
                                        }
                                        notifyDownloadFinished(bundleInfo)
                                    }
                                }
                            } catch (e: Exception) {
                                Timber.e(e)
                                notifyDownloadError(bundleInfo, e)
                            }
                            Timber.d("Coroutine finished execution")
                        }
                    }
                }
            })
    }

    private fun notifyDownloadFinished(bundleInfo: TestDataBundleInfo) =
        CoroutineScope(Dispatchers.Main).launch {
            if (!cancellationFlag.get()) {
                //If it is marked for cancellation, do not post Ui event
                EventBus.getDefault().post(UiProgressEvent.ProgressFinished())
            }
            getListeners().forEach {
                it.onFinishedItemDownload(bundleInfo.videoKey)
            }
        }

    private fun notifyDownloadError(item: TestDataBundleInfo, e: Throwable) =
        CoroutineScope(Dispatchers.Main).launch {
            getListeners().forEach {
                it.onItemDownloadFailed(item, e)
            }
        }

    fun rollbackDownloads(items: List<TestDataBundleInfo>) {
        Timber.d("Rolling back ${items.size} items")
        items.forEach {
            Timber.d("Deleting files for item ${it.videoKey}")
            filesStorageHelper.deleteData(filesStorageHelper.getBaseDirectoryByVideoId(it.videoKey))
            CoroutineScope(Dispatchers.IO).launch {
                Timber.d("Deleting database registry for item ${it.videoKey}")
                tracksRepository.delete(it.videoKey)
            }
        }
    }

    fun markForCancellation() {
        cancellationFlag.set(true)
    }
}