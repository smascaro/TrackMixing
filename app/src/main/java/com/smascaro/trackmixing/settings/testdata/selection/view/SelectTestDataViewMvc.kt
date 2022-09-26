package com.smascaro.trackmixing.settings.testdata.selection.view

import com.smascaro.trackmixing.base.ui.architecture.view.ObservableViewMvc
import com.smascaro.trackmixing.settings.testdata.selection.model.TestDataBundleInfo

interface SelectTestDataViewMvc : ObservableViewMvc<SelectTestDataViewMvc.Listener> {
    interface Listener {
        fun onItemSelected(item: TestDataBundleInfo)
        fun onItemUnselected(item: TestDataBundleInfo)
        fun onDownloadButtonClicked()
    }

    fun showProgress()
    fun hideProgress()
    fun bindTracks(tracks: List<TestDataBundleInfo>)
    fun bindAvailableSpace(availableBytes: Long)
    fun bindAlreadyDownloadedData(downloadedTestData: List<TestDataBundleInfo>)
    fun enableDownloadButton()
    fun disableDownloadButton()
    fun updateSizeToDownload(bytesToDownload: Int)
    fun showError(message: String?)
}