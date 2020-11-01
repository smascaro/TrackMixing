package com.smascaro.trackmixing.common.utils

import com.bumptech.glide.RequestManager
import com.smascaro.trackmixing.search.view.resultitem.SearchResultsItemViewMvc
import com.smascaro.trackmixing.search.view.resultitem.SearchResultsItemViewMvcImpl
import com.smascaro.trackmixing.settings.business.downloadtestdata.selection.view.testdataitem.SelectTestDataItemViewMvc
import com.smascaro.trackmixing.settings.business.downloadtestdata.selection.view.testdataitem.SelectTestDataItemViewMvcImpl
import com.smascaro.trackmixing.trackslist.view.listitem.TracksListItemViewMvc
import com.smascaro.trackmixing.trackslist.view.listitem.TracksListItemViewMvcImpl
import javax.inject.Inject

class DaggerViewMvcFactory @Inject constructor(
    private val glide: RequestManager,
    private val resourcesWrapper: ResourcesWrapper
) {
    fun getTracksListItemViewMvc(): TracksListItemViewMvc {
        return TracksListItemViewMvcImpl(
            glide,
            resourcesWrapper
        )
    }

    fun getSearchResultsItemViewMvc(): SearchResultsItemViewMvc {
        return SearchResultsItemViewMvcImpl(glide, resourcesWrapper)
    }

    fun getSelectTestDataItemViewMvc(): SelectTestDataItemViewMvc {
        return SelectTestDataItemViewMvcImpl(glide)
    }
}