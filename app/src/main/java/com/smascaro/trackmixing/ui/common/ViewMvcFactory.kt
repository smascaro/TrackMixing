package com.smascaro.trackmixing.ui.common

import android.view.LayoutInflater
import android.view.ViewGroup
import com.smascaro.trackmixing.tracks.Track
import com.smascaro.trackmixing.ui.details.TrackDetailsViewMvc
import com.smascaro.trackmixing.ui.details.TrackDetailsViewMvcImpl

class ViewMvcFactory(private val mLayoutInflater: LayoutInflater) {

    private fun getLayoutInflater() = mLayoutInflater

    fun getTrackDetailsViewMvc(
        parent: ViewGroup?,
        track: Track
    ): TrackDetailsViewMvc {
        return TrackDetailsViewMvcImpl(getLayoutInflater(), parent, track)
    }
}