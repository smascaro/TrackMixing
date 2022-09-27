package com.smascaro.trackmixing.settings.testdata.selection.view

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.smascaro.trackmixing.base.utils.asMB
import com.smascaro.trackmixing.databinding.ItemTestDataSelectionBinding
import com.smascaro.trackmixing.settings.testdata.selection.model.TestDataBundleInfo
import javax.inject.Inject

class TestDataListAdapter @Inject constructor() : RecyclerView.Adapter<TestDataListAdapter.ViewHolder>() {
    fun interface Listener {
        fun onItemSelectionChanged(item: TestDataBundleInfo, selected: Boolean)
    }

    private var listener: Listener? = null
    private var testDataList: List<TestDataBundleInfo> = mutableListOf()

    fun setAdapterListener(listener: Listener) {
        this.listener = listener
    }

    fun removeAdapterListener() {
        this.listener = null
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemTestDataSelectionBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return testDataList.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bindData(testDataList[position])
    }

    fun bindData(data: List<TestDataBundleInfo>) {
        testDataList = data
        notifyDataSetChanged()
    }

    inner class ViewHolder(private val binding: ItemTestDataSelectionBinding) :
        RecyclerView.ViewHolder(binding.root) {
        private lateinit var data: TestDataBundleInfo

        private fun setCheckboxListener() {
            binding.cbItemTestDataSelectionSelected.setOnCheckedChangeListener { buttonView, isChecked ->
                listener?.onItemSelectionChanged(data, isChecked)
            }
        }

        private fun removeCheckboxListener() {
            binding.cbItemTestDataSelectionSelected.setOnCheckedChangeListener(null)
        }

        fun bindData(data: TestDataBundleInfo) {
            this.data = data
            binding.tvItemTestDataSelectionTitle.text = data.title
            binding.tvItemTestDataSelectionAuthor.text = data.author
            binding.tvItemTestDataSelectionSize.text = data.size.asMB
            removeCheckboxListener()
            if (data.isPresentInDatabase) {
                binding.cbItemTestDataSelectionSelected.isChecked = true
                binding.cbItemTestDataSelectionSelected.isEnabled = false
            } else {
                binding.cbItemTestDataSelectionSelected.isChecked = false
                binding.cbItemTestDataSelectionSelected.isEnabled = true
            }
            setCheckboxListener()
        }
    }
}