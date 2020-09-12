package com.smascaro.trackmixing.settings.business.downloadtestdata.selection.view.testdataitem

import android.view.View
import com.bumptech.glide.RequestManager
import com.google.android.material.checkbox.MaterialCheckBox
import com.google.android.material.textview.MaterialTextView
import com.smascaro.trackmixing.R
import com.smascaro.trackmixing.common.view.architecture.BaseObservableViewMvc
import com.smascaro.trackmixing.settings.business.downloadtestdata.selection.model.TestDataBundleInfo
import com.smascaro.trackmixing.settings.business.downloadtestdata.selection.model.asMB
import javax.inject.Inject

class SelectTestDataItemViewMvcImpl @Inject constructor(private val glide: RequestManager) :
    BaseObservableViewMvc<SelectTestDataItemViewMvc.Listener>(),
    SelectTestDataItemViewMvc {
    private lateinit var data: TestDataBundleInfo
    private var position: Int = -1

    private lateinit var checkboxSelectDataToDownload: MaterialCheckBox
    private lateinit var dataTitleText: MaterialTextView
    private lateinit var dataAuthorText: MaterialTextView
    private lateinit var dataSizeText: MaterialTextView
    override fun bindRootView(rootView: View?) {
        super.bindRootView(rootView)
        initialize()
    }

    private fun initialize() {
        checkboxSelectDataToDownload = findViewById(R.id.cb_item_test_data_selection_selected)
        dataTitleText = findViewById(R.id.tv_item_test_data_selection_title)
        dataAuthorText = findViewById(R.id.tv_item_test_data_selection_author)
        dataSizeText = findViewById(R.id.tv_item_test_data_selection_size)

        initializeListeners()
    }

    private fun initializeListeners() {
        checkboxSelectDataToDownload.setOnCheckedChangeListener { buttonView, isChecked ->
            getListeners().forEach {
                it.onSelectionCheckChanged(data, isChecked)
            }
        }
    }

    override fun bindData(data: TestDataBundleInfo) {
        this.data = data
        dataTitleText.text = data.title
        dataAuthorText.text = data.author
        dataSizeText.text = data.size.asMB
        if (data.isPresentInDatabase) {
            checkboxSelectDataToDownload.isChecked = true
            checkboxSelectDataToDownload.isEnabled = false
        } else {
            checkboxSelectDataToDownload.isChecked = false
            checkboxSelectDataToDownload.isEnabled = true
        }
    }

    override fun bindPosition(position: Int) {
        this.position = position
    }
}