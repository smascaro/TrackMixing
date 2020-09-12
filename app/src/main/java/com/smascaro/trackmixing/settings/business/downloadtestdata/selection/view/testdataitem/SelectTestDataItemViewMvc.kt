package com.smascaro.trackmixing.settings.business.downloadtestdata.selection.view.testdataitem

import com.smascaro.trackmixing.common.view.architecture.ObservableViewMvc
import com.smascaro.trackmixing.settings.business.downloadtestdata.selection.model.TestDataBundleInfo

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