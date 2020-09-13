package com.smascaro.trackmixing.settings.business.downloadtestdata.selection.view

import com.smascaro.trackmixing.common.view.architecture.ObservableViewMvc
import com.smascaro.trackmixing.settings.business.downloadtestdata.selection.model.TestDataBundleInfo

interface SelectTestDataViewMvc : ObservableViewMvc<SelectTestDataViewMvc.Listener> {
    fun bindTracks(tracks: List<TestDataBundleInfo>)
    fun bindAlreadyDownloadedData(downloadedTestData: List<TestDataBundleInfo>)
    fun showError(message: String?)

    interface Listener {
        fun onItemSelected(item: TestDataBundleInfo)
        fun onItemUnselected(item: TestDataBundleInfo)
        fun onDownloadButtonClicked()
    }
}