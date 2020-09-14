package com.smascaro.trackmixing.settings.business.downloadtestdata.selection.view

import com.smascaro.trackmixing.common.view.architecture.ObservableViewMvc
import com.smascaro.trackmixing.settings.business.downloadtestdata.selection.model.TestDataBundleInfo

interface SelectTestDataViewMvc : ObservableViewMvc<SelectTestDataViewMvc.Listener> {
    interface Listener {
        fun onItemSelected(item: TestDataBundleInfo)
        fun onItemUnselected(item: TestDataBundleInfo)
        fun onDownloadButtonClicked()
    }

    fun bindTracks(tracks: List<TestDataBundleInfo>)
    fun bindAvailableSpace(availableBytes: Long)
    fun bindAlreadyDownloadedData(downloadedTestData: List<TestDataBundleInfo>)
    fun enableDownloadButton()
    fun disableDownloadButton()
    fun updateSizeToDownload(bytesToDownload: Int)
    fun showError(message: String?)
}