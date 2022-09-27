package com.smascaro.trackmixing.di

import com.bumptech.glide.RequestManager
import com.smascaro.trackmixing.base.utils.ResourcesWrapper
import com.smascaro.trackmixing.trackslist.view.listitem.TracksListItemViewMvc
import com.smascaro.trackmixing.trackslist.view.listitem.TracksListItemViewMvcImpl
import javax.inject.Inject

class TracksListItemViewMvcFactory @Inject constructor(
    private val glide: RequestManager,
    private val resourcesWrapper: ResourcesWrapper
) : ItemViewMvcFactory<TracksListItemViewMvc> {
    override fun create(): TracksListItemViewMvc {
        return TracksListItemViewMvcImpl(glide, resourcesWrapper)
    }
}