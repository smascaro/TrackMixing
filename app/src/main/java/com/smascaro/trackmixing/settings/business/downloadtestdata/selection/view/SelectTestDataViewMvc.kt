package com.smascaro.trackmixing.settings.business.downloadtestdata.selection.view

import com.smascaro.trackmixing.common.data.model.Track
import com.smascaro.trackmixing.common.view.architecture.ObservableViewMvc

interface SelectTestDataViewMvc : ObservableViewMvc<SelectTestDataViewMvc.Listener> {
    fun bindTracks(tracks: List<Track>)
    fun showError(message: String?)

    interface Listener
}