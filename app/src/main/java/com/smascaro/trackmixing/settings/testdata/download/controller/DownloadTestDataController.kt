package com.smascaro.trackmixing.settingsOld.testdata.download.controller

import com.smascaro.trackmixing.base.coroutine.IoCoroutineScope
import com.smascaro.trackmixing.base.utils.navigation.BaseNavigatorController
import com.smascaro.trackmixing.base.utils.navigation.NavigationHelper
import com.smascaro.trackmixing.settings.testdata.download.DownloadTestDataUseCase
import com.smascaro.trackmixing.settings.testdata.selection.model.TestDataBundleInfo
import com.smascaro.trackmixing.settingsOld.testdata.download.view.DownloadTestDataViewMvc
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

class DownloadTestDataController @Inject constructor(
    private val downloadTestDataUseCase: DownloadTestDataUseCase,
    private val io: com.smascaro.trackmixing.base.coroutine.IoCoroutineScope,
    navigationHelper: NavigationHelper
) :
    BaseNavigatorController<DownloadTestDataViewMvc>(navigationHelper),
    DownloadTestDataViewMvc.Listener, DownloadTestDataUseCase.Listener {
    interface Listener {
        fun onFinishedFlow()
    }

    private var tracksToDownload: Array<TestDataBundleInfo> = arrayOf()
    private var finishedDownloads = 0
    private var listener: Listener? = null
    private var cancellationFlag: Boolean = false

    fun registerListener(listener: Listener) {
        this.listener = listener
    }

    fun unregisterListener() {
        this.listener = null
    }

    fun onCreate() {
        downloadTestDataUseCase.registerListener(this)
        viewMvc.bindDownloadCount(tracksToDownload.size)
        startDownloads()
    }

    private fun startDownloads() {
        tracksToDownload.forEach {
            downloadTestDataUseCase.downloadItemBundle(it)
        }
    }

    fun bindTracksToDownload(tracks: Array<TestDataBundleInfo>) {
        tracksToDownload = tracks
    }

    override fun onCancelRequest() {
        cancelDownloads()
        viewMvc.showCancellingFeedback()
    }

    override fun onFinishedItemDownload(videoKey: String) {
        finishedDownloads++
        if (!cancellationFlag) {
            viewMvc.updateProgress(finishedDownloads, tracksToDownload.size)
        }
        if (finishedDownloads == tracksToDownload.size) {
            if (!cancellationFlag) {
                viewMvc.hideProgressBar()
                io.launch {
                    delay(500)
                    listener?.onFinishedFlow()
                }
            } else {
                downloadTestDataUseCase.rollbackDownloads(tracksToDownload.toList())
                io.launch {
                    delay(500)
                    listener?.onFinishedFlow()
                }
            }
        }
    }

    override fun onItemDownloadFailed(trackId: String, throwable: Throwable) {
        viewMvc.notifyTrackFailure(trackId, throwable.localizedMessage ?: "Unknown error")
    }

    fun cancelDownloads(): Boolean {
        Timber.d("Will rollback as soon as all downloads have finished")
        downloadTestDataUseCase.markForCancellation()
        viewMvc.showCancellingFeedback()
        cancellationFlag = true
        return cancellationFlag
    }

    override fun dispose() {
        viewMvc.unregisterListener(this)
        listener = null
    }
}