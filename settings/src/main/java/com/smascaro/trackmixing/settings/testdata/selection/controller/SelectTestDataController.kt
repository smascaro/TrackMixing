package com.smascaro.trackmixing.settings.testdata.selection.controller

import com.smascaro.trackmixing.base.coroutine.IoCoroutineScope
import com.smascaro.trackmixing.base.coroutine.MainCoroutineScope
import com.smascaro.trackmixing.base.data.repository.TracksRepository
import com.smascaro.trackmixing.base.utils.navigation.BaseNavigatorController
import com.smascaro.trackmixing.base.utils.navigation.NavigationHelper
import com.smascaro.trackmixing.settings.testdata.selection.view.SelectTestDataFragmentDirections
import com.smascaro.trackmixing.settings.testdata.selection.view.SelectTestDataViewMvc
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

class SelectTestDataController @Inject constructor(
    private val downloadTestDataUseCase: com.smascaro.trackmixing.settings.testdata.download.DownloadTestDataUseCase,
    private val tracksRepository: TracksRepository,
    private val diskSpaceHelper: DiskSpaceHelper,
    private val ui: MainCoroutineScope,
    private val io: IoCoroutineScope,
    navigationHelper: NavigationHelper
) :
    BaseNavigatorController<SelectTestDataViewMvc>(navigationHelper),
    SelectTestDataViewMvc.Listener {
    private var totalDownloadBytes = 0
    private var tracksToDownload =
        mutableListOf<com.smascaro.trackmixing.settings.testdata.selection.model.TestDataBundleInfo>()

    fun onStart() {
        ui.launch {
            viewMvc.showProgress()
            val availableBundlesResult =
                withContext(Dispatchers.IO) { downloadTestDataUseCase.getTestDataBundleInfo() }
            when (availableBundlesResult) {
                is com.smascaro.trackmixing.settings.testdata.download.DownloadTestDataUseCase.Result.Success -> {
                    viewMvc.bindTracks(availableBundlesResult.tracks)
                    checkAlreadyDownloadedItems(availableBundlesResult.tracks)
                }
                is com.smascaro.trackmixing.settings.testdata.download.DownloadTestDataUseCase.Result.Failure -> viewMvc.showError(
                    availableBundlesResult.throwable.localizedMessage
                )
            }
            viewMvc.hideProgress()
        }

        viewMvc.registerListener(this)
        viewMvc.disableDownloadButton()
        viewMvc.bindAvailableSpace(diskSpaceHelper.getAvailableBytes())
    }

    private fun checkAlreadyDownloadedItems(tracks: List<com.smascaro.trackmixing.settings.testdata.selection.model.TestDataBundleInfo>) {
        io.launch {
            val downloads = tracksRepository.getAll()
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

    override fun onItemSelected(item: com.smascaro.trackmixing.settings.testdata.selection.model.TestDataBundleInfo) {
        tracksToDownload.add(item)
        viewMvc.updateSizeToDownload(getTotalSizeToDownload())
        updateDownloadButtonState()
    }

    override fun onItemUnselected(item: com.smascaro.trackmixing.settings.testdata.selection.model.TestDataBundleInfo) {
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
        navigationHelper.navigate(
            SelectTestDataFragmentDirections.actionDestinationSelectTestDataToDownloadTestDataFragment(
                tracksToDownload.toTypedArray()
            )
        )
    }

    private fun getTotalSizeToDownload(): Int {
        return tracksToDownload.sumBy { it.size }
    }

    override fun dispose() {
        viewMvc.unregisterListener(this)
    }
}