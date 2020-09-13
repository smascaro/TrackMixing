package com.smascaro.trackmixing.settings.business.downloadtestdata.download.controller

import com.smascaro.trackmixing.common.controller.BaseNavigatorController
import com.smascaro.trackmixing.common.utils.NavigationHelper
import com.smascaro.trackmixing.settings.business.downloadtestdata.download.view.DownloadTestDataViewMvc
import com.smascaro.trackmixing.settings.business.downloadtestdata.selection.model.TestDataBundleInfo
import com.smascaro.trackmixing.settings.business.downloadtestdata.usecase.DownloadTestDataUseCase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import javax.inject.Inject

class DownloadTestDataController @Inject constructor(
    private val downloadTestDataUseCase: DownloadTestDataUseCase,
    p_navigationHelper: NavigationHelper
) :
    BaseNavigatorController<DownloadTestDataViewMvc>(p_navigationHelper),
    DownloadTestDataViewMvc.Listener, DownloadTestDataUseCase.Listener {
    interface Listener {
        fun onFinishedFlow()
    }

    private var tracksToDownload: Array<TestDataBundleInfo> = arrayOf()
    private var finishedDownloads = 0

    private var listener: Listener? = null

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
        downloadTestDataUseCase.cancelDownloads()
    }

    override fun onFinishedItemDownload(videoKey: String) {
        finishedDownloads++
        viewMvc.updateProgress(finishedDownloads, tracksToDownload.size)
        if (finishedDownloads == tracksToDownload.size) {
            viewMvc.hideProgressBar()
            CoroutineScope(Dispatchers.IO).launch {
                delay(500)
                listener?.onFinishedFlow()
            }

        }
    }

    override fun onItemDownloadFailed(item: TestDataBundleInfo, throwable: Throwable) {
        viewMvc.notifyTrackFailure(item.title, throwable.localizedMessage)
    }
}