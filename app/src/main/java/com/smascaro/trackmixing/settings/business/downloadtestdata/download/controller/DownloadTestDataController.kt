package com.smascaro.trackmixing.settings.business.downloadtestdata.download.controller

import com.smascaro.trackmixing.common.controller.BaseNavigatorController
import com.smascaro.trackmixing.common.utils.NavigationHelper
import com.smascaro.trackmixing.settings.business.downloadtestdata.download.view.DownloadTestDataViewMvc
import com.smascaro.trackmixing.settings.business.downloadtestdata.selection.model.TestDataBundleInfo
import com.smascaro.trackmixing.settings.business.downloadtestdata.usecase.DownloadTestDataUseCase
import javax.inject.Inject

class DownloadTestDataController @Inject constructor(
    private val downloadTestDataUseCase: DownloadTestDataUseCase,
    p_navigationHelper: NavigationHelper
) :
    BaseNavigatorController<DownloadTestDataViewMvc>(p_navigationHelper),
    DownloadTestDataViewMvc.Listener, DownloadTestDataUseCase.Listener {
    private var tracksToDownload: Array<TestDataBundleInfo> = arrayOf()
    private var finishedDownloads = 0
    fun onCreate() {
        downloadTestDataUseCase.registerListener(this)
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
        viewMvc.updateProgress(++finishedDownloads, tracksToDownload.size)
    }

    override fun onItemDownloadFailed(videoKey: String, throwable: Throwable) {
        viewMvc.notifyTrackFailure(videoKey)
    }
}