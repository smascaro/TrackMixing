package com.smascaro.trackmixing.ui.common

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.annotation.Nullable
import com.smascaro.trackmixing.ui.player.TracksPlayerViewMvc
import com.smascaro.trackmixing.ui.player.TracksPlayerViewMvcImpl
import com.smascaro.trackmixing.tracks.Track
import com.smascaro.trackmixing.ui.common.navigationhelper.BottomNavigationViewMvc
import com.smascaro.trackmixing.ui.common.navigationhelper.BottomNavigationViewMvcImpl
import com.smascaro.trackmixing.ui.details.TrackDetailsViewMvc
import com.smascaro.trackmixing.ui.details.TrackDetailsViewMvcImpl
import com.smascaro.trackmixing.ui.trackslist.TracksListViewMvc
import com.smascaro.trackmixing.ui.trackslist.TracksListViewMvcImpl
import com.smascaro.trackmixing.ui.trackslist.trackslistitem.TracksListItemViewMvc
import com.smascaro.trackmixing.ui.trackslist.trackslistitem.TracksListItemViewMvcImpl

class ViewMvcFactory(private val mLayoutInflater: LayoutInflater) {
    fun getTracksListViewMvc(@Nullable parent: ViewGroup?): TracksListViewMvc {
        return TracksListViewMvcImpl(
            mLayoutInflater,
            parent,
            this
        )
    }

    private fun getLayoutInflater() = mLayoutInflater
    fun getTracksListItemViewMvc(parent: ViewGroup?): TracksListItemViewMvc {
        return TracksListItemViewMvcImpl(getLayoutInflater(), parent)
    }

    fun getBottomNavigationViewMvc(parent: ViewGroup?): BottomNavigationViewMvc {
        return BottomNavigationViewMvcImpl(getLayoutInflater(), parent)
    }

    fun getTrackDetailsViewMvc(
        parent: ViewGroup?,
        track: Track
    ): TrackDetailsViewMvc {
        return TrackDetailsViewMvcImpl(getLayoutInflater(), parent, track)
    }

    fun getTracksPlayerViewMvc(
        parent: ViewGroup?
    ): TracksPlayerViewMvc {
        return TracksPlayerViewMvcImpl(
            getLayoutInflater(),
            parent
        )
    }
}