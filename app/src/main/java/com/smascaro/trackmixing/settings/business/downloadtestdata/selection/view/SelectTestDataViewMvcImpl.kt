package com.smascaro.trackmixing.settings.business.downloadtestdata.selection.view

import android.view.View
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.SimpleItemAnimator
import com.google.android.material.button.MaterialButton
import com.google.android.material.textview.MaterialTextView
import com.smascaro.trackmixing.R
import com.smascaro.trackmixing.common.view.architecture.BaseObservableViewMvc
import com.smascaro.trackmixing.settings.business.downloadtestdata.selection.model.TestDataBundleInfo
import com.smascaro.trackmixing.settings.business.downloadtestdata.selection.model.asMB
import javax.inject.Inject

class SelectTestDataViewMvcImpl @Inject constructor(
    private val testDataAdapter: TestDataListAdapter
) :
    BaseObservableViewMvc<SelectTestDataViewMvc.Listener>(),
    SelectTestDataViewMvc, TestDataListAdapter.Listener {
    private lateinit var totalDownloadSizeText: MaterialTextView
    private lateinit var recyclerViewTestDataBundleInfo: RecyclerView
    private lateinit var startDownloadButton: MaterialButton

    override fun bindRootView(rootView: View?) {
        super.bindRootView(rootView)
        initialize()
    }

    private fun initialize() {
        recyclerViewTestDataBundleInfo = findViewById(R.id.rv_select_test_data_tracks)
        recyclerViewTestDataBundleInfo.layoutManager = LinearLayoutManager(getContext())
        (recyclerViewTestDataBundleInfo.itemAnimator as SimpleItemAnimator).supportsChangeAnimations =
            false
        recyclerViewTestDataBundleInfo.setHasFixedSize(true)
        testDataAdapter.setAdapterListener(this)
        recyclerViewTestDataBundleInfo.adapter = testDataAdapter

        startDownloadButton = findViewById(R.id.btn_select_test_data_start_download)
        totalDownloadSizeText = findViewById(R.id.tv_select_test_data_total_size)
        startDownloadButton.setOnClickListener {
            getListeners().forEach {
                it.onDownloadButtonClicked()
            }
        }
        updateTotalSize(0)
    }

    override fun bindTracks(tracks: List<TestDataBundleInfo>) {
        testDataAdapter.bindData(tracks)
    }

    override fun bindAlreadyDownloadedData(downloadedTestData: List<TestDataBundleInfo>) {
        testDataAdapter.bindAlreadyDownloadedData(downloadedTestData)
    }

    override fun updateSizeToDownload(bytesToDownload: Int) {
        updateTotalSize(bytesToDownload)
    }

    override fun showError(message: String?) {
        Toast.makeText(getContext(), "Error: $message", Toast.LENGTH_SHORT).show()
    }

    override fun onItemSelectionChanged(
        item: TestDataBundleInfo,
        selected: Boolean
    ) {
        getListeners().forEach {
            if (selected) {
                it.onItemSelected(item)
            } else {
                it.onItemUnselected(item)
            }
        }
    }

    private fun updateTotalSize(bytes: Int) {
        totalDownloadSizeText.text = bytes.asMB
    }
}