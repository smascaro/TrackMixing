package com.smascaro.trackmixing.search.model.repository

import com.smascaro.trackmixing.search.model.SearchResult
import com.smascaro.trackmixing.search.model.repository.datasource.SearchResultsNetworkDataSource
import javax.inject.Inject

class SearchResultsRepository @Inject constructor(
    private val searchResultsNetworkDataSource: SearchResultsNetworkDataSource
) {
    suspend fun query(query: String): List<SearchResult> {
        //Check cache
        //Api request if not cached
        val intermediate = searchResultsNetworkDataSource.query(query)
        return searchResultsNetworkDataSource.fetchAndMapDetailsFromSearchResponse(intermediate)
    }
}