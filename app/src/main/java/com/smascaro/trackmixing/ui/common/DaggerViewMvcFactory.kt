package com.smascaro.trackmixing.ui.common

import com.bumptech.glide.RequestManager
import com.smascaro.trackmixing.ui.trackslist.trackslistitem.TracksListItemViewMvc
import com.smascaro.trackmixing.ui.trackslist.trackslistitem.TracksListItemViewMvcImpl
import javax.inject.Inject

class DaggerViewMvcFactory @Inject constructor(val glide: RequestManager) {
    fun getTracksListItemViewMvc(): TracksListItemViewMvc {
        return TracksListItemViewMvcImpl(glide)
    }
}