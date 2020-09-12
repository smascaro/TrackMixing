package com.smascaro.trackmixing.settings.business.downloadtestdata.selection.view

import android.widget.Toast
import com.smascaro.trackmixing.common.view.architecture.BaseObservableViewMvc
import com.smascaro.trackmixing.settings.business.downloadtestdata.selection.model.TestDataBundleInfo
import javax.inject.Inject

class SelectTestDataViewMvcImpl @Inject constructor() :
    BaseObservableViewMvc<SelectTestDataViewMvc.Listener>(),
    SelectTestDataViewMvc {
    override fun bindTracks(tracks: List<TestDataBundleInfo>) {
        Toast.makeText(getContext(), "Binding ${tracks.size} tracks", Toast.LENGTH_SHORT).show()
    }

    override fun showError(message: String?) {
        Toast.makeText(getContext(), "Error: $message", Toast.LENGTH_SHORT).show()
    }
}