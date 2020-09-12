package com.smascaro.trackmixing.settings.business.downloadtestdata.selection.view

import android.view.View
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.SimpleItemAnimator
import com.smascaro.trackmixing.R
import com.smascaro.trackmixing.common.view.architecture.BaseObservableViewMvc
import com.smascaro.trackmixing.settings.business.downloadtestdata.selection.model.TestDataBundleInfo
import javax.inject.Inject

class SelectTestDataViewMvcImpl @Inject constructor(
    private val testDataAdapter: TestDataListAdapter
) :
    BaseObservableViewMvc<SelectTestDataViewMvc.Listener>(),
    SelectTestDataViewMvc, TestDataListAdapter.Listener {
    private lateinit var recyclerViewTestDataBundleInfo: RecyclerView
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
    }

    override fun bindTracks(tracks: List<TestDataBundleInfo>) {
        Toast.makeText(getContext(), "Binding ${tracks.size} tracks", Toast.LENGTH_SHORT).show()
        testDataAdapter.bindData(tracks)
    }

    override fun bindAlreadyDownloadedData(downloadedTestData: List<TestDataBundleInfo>) {
        Toast.makeText(
            getContext(),
            "${downloadedTestData.size} tracks already downloaded",
            Toast.LENGTH_SHORT
        ).show()
        testDataAdapter.bindAlreadyDownloadedData(downloadedTestData)
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
}