package com.smascaro.trackmixing.search.model.repository

import com.smascaro.trackmixing.search.model.repository.datasource.SearchResultsNetworkDataSource
import javax.inject.Inject

class SearchResultsRepository @Inject constructor(
    private val searchResultsNetworkDataSource: SearchResultsNetworkDataSource
) {
    fun query(query: String) {
        //Check cache

        //Api request if not cached

        searchResultsNetworkDataSource.queryAndNotify(query)

    }
}