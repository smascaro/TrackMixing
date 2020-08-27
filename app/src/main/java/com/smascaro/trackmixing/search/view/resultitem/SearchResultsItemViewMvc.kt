package com.smascaro.trackmixing.search.view.resultitem

import com.smascaro.trackmixing.common.view.architecture.ObservableViewMvc
import com.smascaro.trackmixing.search.model.SearchResult

interface SearchResultsItemViewMvc : ObservableViewMvc<SearchResultsItemViewMvc.Listener> {
    interface Listener {
        fun onSearchResultClicked(result: SearchResult)
    }

    fun bindResult(result: SearchResult)
    fun bindPosition(position: Int)
}