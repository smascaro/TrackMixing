package com.smascaro.trackmixing.settings.testdata.selection.view.testdataitem

import com.smascaro.trackmixing.base.ui.architecture.view.ObservableViewMvc
import com.smascaro.trackmixing.settings.testdata.selection.model.TestDataBundleInfo

interface SelectTestDataItemViewMvc : ObservableViewMvc<SelectTestDataItemViewMvc.Listener> {
    interface Listener {
        fun onSelectionCheckChanged(
            item: TestDataBundleInfo,
            checked: Boolean
        )
    }

    fun bindData(data: TestDataBundleInfo)
    fun bindPosition(position: Int)
}