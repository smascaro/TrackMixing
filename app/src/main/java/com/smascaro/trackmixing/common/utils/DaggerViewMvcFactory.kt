package com.smascaro.trackmixing.common.utils

import com.bumptech.glide.RequestManager
import com.smascaro.trackmixing.search.view.resultitem.SearchResultsItemViewMvc
import com.smascaro.trackmixing.search.view.resultitem.SearchResultsItemViewMvcImpl
import com.smascaro.trackmixing.trackslist.view.listitem.TracksListItemViewMvc
import com.smascaro.trackmixing.trackslist.view.listitem.TracksListItemViewMvcImpl
import javax.inject.Inject

class DaggerViewMvcFactory @Inject constructor(
    private val glide: RequestManager
) {
    fun getTracksListItemViewMvc(): TracksListItemViewMvc {
        return TracksListItemViewMvcImpl(
            glide
        )
    }

    fun getSearchResultsItemViewMvc(): SearchResultsItemViewMvc {
        return SearchResultsItemViewMvcImpl(glide)
    }
}