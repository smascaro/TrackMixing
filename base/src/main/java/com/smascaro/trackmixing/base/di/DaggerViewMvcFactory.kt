package com.smascaro.trackmixing.base.di

import com.bumptech.glide.RequestManager
import com.smascaro.trackmixing.base.utils.ResourcesWrapper
import javax.inject.Inject

class DaggerViewMvcFactory @Inject constructor(
    private val glide: RequestManager,
    private val resourcesWrapper: ResourcesWrapper
) {
    // fun getTracksListItemViewMvc(): TracksListItemViewMvc {
    //     return TracksListItemViewMvcImpl(
    //         glide,
    //         resourcesWrapper
    //     )
    // }
    //
    // fun getSearchResultsItemViewMvc(): SearchResultsItemViewMvc {
    //     return SearchResultsItemViewMvcImpl(glide, resourcesWrapper)
    // }
}