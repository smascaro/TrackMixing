package com.smascaro.trackmixing.settings.business.downloadtestdata.download.view

import com.smascaro.trackmixing.common.view.architecture.ObservableViewMvc

interface DownloadTestDataViewMvc : ObservableViewMvc<DownloadTestDataViewMvc.Listener> {
    interface Listener {
        fun onCancelRequest()
    }

    fun updateProgress(completed: Int, finished: Int)
    fun showProgressBar()
    fun hideProgressBar()
    fun notifyTrackFailure(videoKey: String)
}