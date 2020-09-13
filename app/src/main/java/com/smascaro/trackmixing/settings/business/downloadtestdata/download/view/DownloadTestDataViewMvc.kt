package com.smascaro.trackmixing.settings.business.downloadtestdata.download.view

import com.smascaro.trackmixing.common.view.architecture.ObservableViewMvc

interface DownloadTestDataViewMvc : ObservableViewMvc<DownloadTestDataViewMvc.Listener> {
    interface Listener {
        fun onCancelRequest()
    }

    fun bindDownloadCount(itemsToDownload: Int)
    fun updateProgress(completed: Int, total: Int)
    fun showProgressBar()
    fun hideProgressBar()
    fun notifyTrackFailure(title: String, message: String)
}