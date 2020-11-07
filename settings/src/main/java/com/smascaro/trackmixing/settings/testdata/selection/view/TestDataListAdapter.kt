package com.smascaro.trackmixing.settings.testdata.selection.view

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.smascaro.trackmixing.settings.R
import com.smascaro.trackmixing.settings.di.TestDataItemViewMvcFactory
import com.smascaro.trackmixing.settings.testdata.selection.model.TestDataBundleInfo
import com.smascaro.trackmixing.settings.testdata.selection.view.testdataitem.SelectTestDataItemViewMvc
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

class TestDataListAdapter @Inject constructor(
    private val viewMvcFactory: TestDataItemViewMvcFactory,
) : RecyclerView.Adapter<TestDataListAdapter.ViewHolder>(), SelectTestDataItemViewMvc.Listener {
    interface Listener {
        fun onItemSelectionChanged(
            item: TestDataBundleInfo,
            selected: Boolean
        )
    }

    class ViewHolder(val viewMvc: SelectTestDataItemViewMvc) :
        RecyclerView.ViewHolder(viewMvc.getRootView())

    private var listener: Listener? = null
    private var testDataList: List<TestDataBundleInfo> = mutableListOf()
    fun setAdapterListener(listener: Listener) {
        this.listener = listener
    }

    fun removeAdapterListener() {
        this.listener = null
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val viewMvc = viewMvcFactory.create()
        viewMvc.bindRootView(
            LayoutInflater.from(parent.context)
                .inflate(R.layout.item_test_data_selection, parent, false)
        )
        viewMvc.registerListener(this)
        return ViewHolder(viewMvc)
    }

    override fun getItemCount(): Int {
        return testDataList.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.viewMvc.bindData(testDataList[position])
        holder.viewMvc.bindPosition(position)
    }

    override fun onSelectionCheckChanged(
        item: TestDataBundleInfo,
        checked: Boolean
    ) {
        listener?.onItemSelectionChanged(item, checked)
    }

    fun bindData(data: List<TestDataBundleInfo>) {
        testDataList = data
        CoroutineScope(Dispatchers.Main).launch {
            notifyDataSetChanged()
        }
    }

    fun bindAlreadyDownloadedData(downloadedTestData: List<TestDataBundleInfo>) {
        downloadedTestData.forEach {
            val idx = testDataList.indexOfFirst { it2 -> it.videoKey == it2.videoKey }
            testDataList[idx].isPresentInDatabase = true
            notifyItemChanged(idx)
        }
    }
}