package com.smascaro.trackmixing.settings.business.downloadtestdata.selection.controller

import com.smascaro.trackmixing.base.coroutine.IoCoroutineScope
import com.smascaro.trackmixing.base.coroutine.MainCoroutineScope
import com.smascaro.trackmixing.common.controller.BaseNavigatorController
import com.smascaro.trackmixing.common.data.datasource.repository.DownloadsDao
import com.smascaro.trackmixing.common.utils.DiskSpaceHelper
import com.smascaro.trackmixing.common.utils.navigation.NavigationHelper
import com.smascaro.trackmixing.settings.business.downloadtestdata.selection.model.TestDataBundleInfo
import com.smascaro.trackmixing.settings.business.downloadtestdata.selection.view.SelectTestDataViewMvc
import com.smascaro.trackmixing.settings.business.downloadtestdata.usecase.DownloadTestDataUseCase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

class SelectTestDataController @Inject constructor(
    private val downloadTestDataUseCase: DownloadTestDataUseCase,
    private val downloadsDao: DownloadsDao,
    private val diskSpaceHelper: DiskSpaceHelper,
    private val ui: MainCoroutineScope,
    private val io: IoCoroutineScope,
    p_navigationHelper: NavigationHelper
) :
    BaseNavigatorController<SelectTestDataViewMvc>(p_navigationHelper),
    SelectTestDataViewMvc.Listener {
    private var totalDownloadBytes = 0
    private var tracksToDownload = mutableListOf<TestDataBundleInfo>()
    fun onStart() {
        ui.launch {
            viewMvc.showProgress()
            val availableBundlesResult =
                withContext(Dispatchers.IO) { downloadTestDataUseCase.getTestDataBundleInfo() }
            when (availableBundlesResult) {
                is DownloadTestDataUseCase.Result.Success -> {
                    viewMvc.bindTracks(availableBundlesResult.tracks)
                    checkAlreadyDownloadedItems(availableBundlesResult.tracks)
                }
                is DownloadTestDataUseCase.Result.Failure -> viewMvc.showError(
                    availableBundlesResult.throwable.localizedMessage
                )
            }
            viewMvc.hideProgress()
        }

        viewMvc.registerListener(this)
        viewMvc.disableDownloadButton()
        viewMvc.bindAvailableSpace(diskSpaceHelper.getAvailableBytes())
    }

    private fun checkAlreadyDownloadedItems(tracks: List<TestDataBundleInfo>) {
        io.launch {
            val downloads = downloadsDao.getAll()
            val downloadedTestData = tracks.filter { testDataItem ->
                downloads.contains {
                    it.sourceVideoKey == testDataItem.videoKey
                }
            }
            ui.launch {
                viewMvc.bindAlreadyDownloadedData(downloadedTestData)
            }
        }
    }

    inline fun <T> Iterable<T>.contains(predicate: (elem: T) -> Boolean): Boolean {
        return this.any { predicate(it) }
    }

    fun onStop() {
        viewMvc.unregisterListener(this)
    }

    override fun onItemSelected(item: TestDataBundleInfo) {
        tracksToDownload.add(item)
        viewMvc.updateSizeToDownload(getTotalSizeToDownload())
        updateDownloadButtonState()
    }

    override fun onItemUnselected(item: TestDataBundleInfo) {
        tracksToDownload.remove(item)
        viewMvc.updateSizeToDownload(getTotalSizeToDownload())
        updateDownloadButtonState()
    }

    private fun updateDownloadButtonState() {
        if (tracksToDownload.any() && getTotalSizeToDownload() < diskSpaceHelper.getAvailableBytes()) {
            viewMvc.enableDownloadButton()
        } else {
            viewMvc.disableDownloadButton()
        }
    }

    override fun onDownloadButtonClicked() {
        navigationHelper.toTestDataDownload(tracksToDownload)
    }

    private fun getTotalSizeToDownload(): Int {
        return tracksToDownload.sumBy { it.size }
    }

    override fun dispose() {
        viewMvc.unregisterListener(this)
    }
}