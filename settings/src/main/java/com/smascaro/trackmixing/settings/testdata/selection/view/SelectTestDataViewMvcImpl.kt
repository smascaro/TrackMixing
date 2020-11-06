package com.smascaro.trackmixing.settings.testdata.selection.view

import android.graphics.Color
import android.view.View
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.SimpleItemAnimator
import com.facebook.shimmer.ShimmerFrameLayout
import com.google.android.material.button.MaterialButton
import com.google.android.material.textview.MaterialTextView
import com.smascaro.trackmixing.base.ui.architecture.view.BaseObservableViewMvc
import com.smascaro.trackmixing.base.utils.ResourcesWrapper
import com.smascaro.trackmixing.base.utils.asGB
import com.smascaro.trackmixing.base.utils.asKB
import com.smascaro.trackmixing.base.utils.asMB
import com.smascaro.trackmixing.settings.R
import com.smascaro.trackmixing.settings.testdata.selection.model.TestDataBundleInfo
import javax.inject.Inject

class SelectTestDataViewMvcImpl @Inject constructor(
    private val testDataAdapter: TestDataListAdapter,
    private val resourcesWrapper: ResourcesWrapper
) :
    BaseObservableViewMvc<SelectTestDataViewMvc.Listener>(),
    SelectTestDataViewMvc, TestDataListAdapter.Listener {
    private lateinit var totalDownloadSizeText: MaterialTextView
    private lateinit var recyclerViewTestDataBundleInfo: RecyclerView
    private lateinit var startDownloadButton: MaterialButton
    private lateinit var availableSpaceTextView: MaterialTextView
    private lateinit var shimmerContainer: ShimmerFrameLayout

    private var availableBytes = Long.MAX_VALUE
    private var defaultMaterialTextColor: Int = 0

    override fun initialize() {
        super.initialize()
        recyclerViewTestDataBundleInfo = findViewById(R.id.rv_select_test_data_tracks)
        recyclerViewTestDataBundleInfo.layoutManager = LinearLayoutManager(getContext())
        (recyclerViewTestDataBundleInfo.itemAnimator as SimpleItemAnimator).supportsChangeAnimations =
            false
        recyclerViewTestDataBundleInfo.setHasFixedSize(true)
        testDataAdapter.setAdapterListener(this)
        recyclerViewTestDataBundleInfo.adapter = testDataAdapter

        startDownloadButton = findViewById(R.id.btn_select_test_data_start_download)
        totalDownloadSizeText = findViewById(R.id.tv_select_test_data_total_size)
        availableSpaceTextView = findViewById(R.id.tv_select_test_data_available_space)
        shimmerContainer = findViewById(R.id.shimmer_container_test_data)

        defaultMaterialTextColor = totalDownloadSizeText.textColors.defaultColor
        startDownloadButton.setOnClickListener {
            getListeners().forEach {
                it.onDownloadButtonClicked()
            }
        }

        availableSpaceTextView.text = ""
        updateTotalSize(0)
    }

    override fun showProgress() {
        shimmerContainer.visibility = View.VISIBLE
        shimmerContainer.startShimmer()
    }

    override fun hideProgress() {
        shimmerContainer.visibility = View.GONE
        shimmerContainer.stopShimmer()
    }

    override fun bindTracks(tracks: List<TestDataBundleInfo>) {
        testDataAdapter.bindData(tracks)
    }

    override fun bindAvailableSpace(availableBytes: Long) {
        this.availableBytes = availableBytes
        var text = ""
        if (availableBytes > 1 * 1000 * 1000 * 1000) {
            text = availableBytes.asGB
        } else if (availableBytes > 1 * 1000 * 1000) {
            text = availableBytes.asMB
        } else if (availableBytes > 1 * 1000) {
            text = availableBytes.asKB
        } else {
            text = "$availableBytes bytes"
        }

        availableSpaceTextView.text =
            resourcesWrapper.getString(R.string.select_test_data_available_space, text)
    }

    override fun bindAlreadyDownloadedData(downloadedTestData: List<TestDataBundleInfo>) {
        testDataAdapter.bindAlreadyDownloadedData(downloadedTestData)
    }

    override fun enableDownloadButton() {
        startDownloadButton.isEnabled = true
    }

    override fun disableDownloadButton() {
        startDownloadButton.isEnabled = false
    }

    override fun updateSizeToDownload(bytesToDownload: Int) {
        updateTotalSize(bytesToDownload)
        if (bytesToDownload > availableBytes) {
            totalDownloadSizeText.setTextColor(Color.RED)
        } else {
            totalDownloadSizeText.setTextColor(defaultMaterialTextColor)
        }
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