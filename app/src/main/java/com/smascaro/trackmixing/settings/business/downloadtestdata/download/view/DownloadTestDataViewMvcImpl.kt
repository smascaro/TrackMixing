package com.smascaro.trackmixing.settings.business.downloadtestdata.download.view

import android.widget.Toast
import com.smascaro.trackmixing.common.view.architecture.BaseObservableViewMvc
import timber.log.Timber
import javax.inject.Inject

class DownloadTestDataViewMvcImpl @Inject constructor() :
    BaseObservableViewMvc<DownloadTestDataViewMvc.Listener>(), DownloadTestDataViewMvc {
    override fun updateProgress(completed: Int, finished: Int) {
        val message = "Total completed: $completed/$finished"
        Timber.d(message)
        Toast.makeText(getContext(), message, Toast.LENGTH_SHORT).show()
    }

    override fun showProgressBar() {
    }

    override fun hideProgressBar() {
    }

    override fun notifyTrackFailure(videoKey: String) {
        val message = "Track $videoKey failed"
        Timber.w(message)
        Toast.makeText(getContext(), message, Toast.LENGTH_SHORT).show()
    }
}