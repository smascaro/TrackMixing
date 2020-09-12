package com.smascaro.trackmixing.settings.business.downloadtestdata.selection.view

import com.smascaro.trackmixing.common.view.architecture.ObservableViewMvc
import com.smascaro.trackmixing.settings.business.downloadtestdata.selection.model.TestDataBundleInfo

interface SelectTestDataViewMvc : ObservableViewMvc<SelectTestDataViewMvc.Listener> {
    fun bindTracks(tracks: List<TestDataBundleInfo>)
    fun showError(message: String?)

    interface Listener
}