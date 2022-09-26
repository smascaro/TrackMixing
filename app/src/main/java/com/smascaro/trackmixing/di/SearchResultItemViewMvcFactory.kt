package com.smascaro.trackmixing.di

import com.bumptech.glide.RequestManager
import com.smascaro.trackmixing.base.di.ItemViewMvcFactory
import com.smascaro.trackmixing.base.utils.ResourcesWrapper
import com.smascaro.trackmixing.search.view.resultitem.SearchResultsItemViewMvc
import com.smascaro.trackmixing.search.view.resultitem.SearchResultsItemViewMvcImpl
import javax.inject.Inject

class SearchResultItemViewMvcFactory @Inject constructor(
    private val glide: RequestManager,
    private val resourcesWrapper: ResourcesWrapper
) : ItemViewMvcFactory<SearchResultsItemViewMvc> {
    override fun create(): SearchResultsItemViewMvc {
        return SearchResultsItemViewMvcImpl(glide, resourcesWrapper)
    }
}