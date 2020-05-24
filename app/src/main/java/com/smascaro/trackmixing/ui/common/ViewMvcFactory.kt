package com.smascaro.trackmixing.ui.common

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.annotation.Nullable
import androidx.navigation.NavController
import com.smascaro.trackmixing.tracks.Track
import com.smascaro.trackmixing.ui.common.navigationhelper.BottomNavigationViewMvc
import com.smascaro.trackmixing.ui.common.navigationhelper.BottomNavigationViewMvcImpl
import com.smascaro.trackmixing.ui.details.TrackDetailsViewMvc
import com.smascaro.trackmixing.ui.details.TrackDetailsViewMvcImpl
import com.smascaro.trackmixing.ui.trackslist.trackslistitem.TracksListItemViewMvc
import com.smascaro.trackmixing.ui.trackslist.trackslistitem.TracksListItemViewMvcImpl
import com.smascaro.trackmixing.ui.trackslist.TracksListViewMvc
import com.smascaro.trackmixing.ui.trackslist.TracksListViewMvcImpl

class ViewMvcFactory(private val mLayoutInflater: LayoutInflater) {
    fun getTracksListViewMvc(@Nullable parent: ViewGroup?): TracksListViewMvc {
        return TracksListViewMvcImpl(
            mLayoutInflater,
            parent,
            this
        )
    }

    fun getTracksListItemViewMvc(parent: ViewGroup?): TracksListItemViewMvc {
        return TracksListItemViewMvcImpl(mLayoutInflater, parent)
    }

    fun getBottomNavigationViewMvc(parent: ViewGroup?): BottomNavigationViewMvc {
        return BottomNavigationViewMvcImpl(mLayoutInflater, parent)
    }

    fun getTrackDetailsViewMvc(
        parent: ViewGroup?,
        track: Track
    ): TrackDetailsViewMvc {
        return TrackDetailsViewMvcImpl(mLayoutInflater, parent, track)
    }
}