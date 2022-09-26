package com.smascaro.trackmixing.settings.testdata.selection.view.testdataitem

import com.google.android.material.checkbox.MaterialCheckBox
import com.google.android.material.textview.MaterialTextView
import com.smascaro.trackmixing.base.ui.architecture.view.BaseObservableViewMvc
import com.smascaro.trackmixing.base.utils.asMB
import com.smascaro.trackmixing.settings.R
import com.smascaro.trackmixing.settings.testdata.selection.model.TestDataBundleInfo
import javax.inject.Inject

class SelectTestDataItemViewMvcImpl @Inject constructor() :
    BaseObservableViewMvc<SelectTestDataItemViewMvc.Listener>(),
    SelectTestDataItemViewMvc {
    private lateinit var data: TestDataBundleInfo
    private var position: Int = -1
    private lateinit var checkboxSelectDataToDownload: MaterialCheckBox
    private lateinit var dataTitleText: MaterialTextView
    private lateinit var dataAuthorText: MaterialTextView
    private lateinit var dataSizeText: MaterialTextView

    override fun initialize() {
        super.initialize()
        checkboxSelectDataToDownload = findViewById(R.id.cb_item_test_data_selection_selected)
        dataTitleText = findViewById(R.id.tv_item_test_data_selection_title)
        dataAuthorText = findViewById(R.id.tv_item_test_data_selection_author)
        dataSizeText = findViewById(R.id.tv_item_test_data_selection_size)
    }

    override fun initializeListeners() {
        super.initializeListeners()
        setCheckboxListener()
    }

    private fun setCheckboxListener() {
        checkboxSelectDataToDownload.setOnCheckedChangeListener { buttonView, isChecked ->
            getListeners().forEach {
                it.onSelectionCheckChanged(data, isChecked)
            }
        }
    }

    private fun removeCheckboxListener() {
        checkboxSelectDataToDownload.setOnCheckedChangeListener(null)
    }

    override fun bindData(data: TestDataBundleInfo) {
        this.data = data
        dataTitleText.text = data.title
        dataAuthorText.text = data.author
        dataSizeText.text = data.size.asMB
        removeCheckboxListener()
        if (data.isPresentInDatabase) {
            checkboxSelectDataToDownload.isChecked = true
            checkboxSelectDataToDownload.isEnabled = false
        } else {
            checkboxSelectDataToDownload.isChecked = false
            checkboxSelectDataToDownload.isEnabled = true
        }
        setCheckboxListener()
    }

    override fun bindPosition(position: Int) {
        this.position = position
    }
}