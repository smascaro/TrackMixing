package com.smascaro.trackmixing.common.utils

import com.bumptech.glide.RequestManager
import com.smascaro.trackmixing.playbackservice.utils.PlaybackSession
import com.smascaro.trackmixing.trackslist.view.listitem.TracksListItemViewMvc
import com.smascaro.trackmixing.trackslist.view.listitem.TracksListItemViewMvcImpl
import javax.inject.Inject

class DaggerViewMvcFactory @Inject constructor(
    val glide: RequestManager,
    val resourcesWrapper: ResourcesWrapper,
    val playbackSession: PlaybackSession
) {
    fun getTracksListItemViewMvc(): TracksListItemViewMvc {
        return TracksListItemViewMvcImpl(
            glide,
            playbackSession,
            resourcesWrapper
        )
    }
}