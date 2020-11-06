package com.smascaro.trackmixing.settings.testdata.download.view

import android.view.View
import android.widget.Toast
import androidx.core.widget.ContentLoadingProgressBar
import com.google.android.material.textview.MaterialTextView
import com.smascaro.trackmixing.base.ui.architecture.view.BaseObservableViewMvc
import com.smascaro.trackmixing.base.ui.widget.PivotableSeekbar
import com.smascaro.trackmixing.settings.R
import com.smascaro.trackmixing.settingsOld.testdata.download.view.DownloadTestDataViewMvc
import timber.log.Timber
import javax.inject.Inject

class DownloadTestDataViewMvcImpl @Inject constructor() :
    BaseObservableViewMvc<DownloadTestDataViewMvc.Listener>(), DownloadTestDataViewMvc {
    private lateinit var currentStateTitleTextView: MaterialTextView
    private lateinit var downloadingProgressBar: ContentLoadingProgressBar
    private lateinit var downloadProgressSeekbar: PivotableSeekbar
    private lateinit var downloadProgressTextFeedback: MaterialTextView

    override fun initialize() {
        super.initialize()
        currentStateTitleTextView = findViewById(R.id.tv_download_test_data_downloading_title)
        downloadingProgressBar = findViewById(R.id.pb_download_test_data_progress)
        downloadProgressSeekbar = findViewById(R.id.sb_download_test_data_progress)
        downloadProgressTextFeedback = findViewById(R.id.tv_download_test_data_downloading_progress)
    }

    override fun bindDownloadCount(itemsToDownload: Int) {
        downloadProgressTextFeedback.text = "(0/$itemsToDownload)"
        downloadProgressSeekbar.max = itemsToDownload
    }

    override fun updateProgress(completed: Int, total: Int) {
        val message = "Total completed: $completed/$total"
        Timber.d(message)
        if (downloadProgressSeekbar.max == 0) {
            downloadProgressSeekbar.max = total
        }
        downloadProgressSeekbar.progress = completed
        downloadProgressTextFeedback.text = "($completed/$total)"
    }

    override fun showCancellingFeedback() {
        currentStateTitleTextView.text = "Cancelling downloads..."
    }

    override fun showProgressBar() {
        downloadingProgressBar.show()
    }

    override fun hideProgressBar() {
        downloadingProgressBar.visibility = View.INVISIBLE
    }

    override fun notifyTrackFailure(title: String, reason: String) {
        val message = "Track $title failed. Reason: $reason"
        Timber.w(message)
        Toast.makeText(getContext(), message, Toast.LENGTH_SHORT).show()
    }
}