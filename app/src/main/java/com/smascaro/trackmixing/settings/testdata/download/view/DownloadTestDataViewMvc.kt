package com.smascaro.trackmixing.settingsOld.testdata.download.view

import com.smascaro.trackmixing.base.ui.architecture.view.ObservableViewMvc

interface DownloadTestDataViewMvc : ObservableViewMvc<DownloadTestDataViewMvc.Listener> {
    interface Listener {
        fun onCancelRequest()
    }

    fun bindDownloadCount(itemsToDownload: Int)
    fun updateProgress(completed: Int, total: Int)
    fun showCancellingFeedback()
    fun showProgressBar()
    fun hideProgressBar()
    fun notifyTrackFailure(title: String, message: String)
}