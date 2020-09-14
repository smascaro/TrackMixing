package com.smascaro.trackmixing.search.view

import com.smascaro.trackmixing.common.view.architecture.ObservableViewMvc
import com.smascaro.trackmixing.search.model.SearchResult

interface SearchResultsViewMvc : ObservableViewMvc<SearchResultsViewMvc.Listener> {
    interface Listener {
        fun onSearchButtonClicked(currentText: String)
        fun onSearchResultClicked(searchResult: SearchResult)
    }

    fun bindResults(results: List<SearchResult>)
    fun showMessage(message: String)
    fun startRequest(url: String)
}