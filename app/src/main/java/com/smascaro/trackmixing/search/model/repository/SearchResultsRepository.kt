package com.smascaro.trackmixing.search.model.repository

import com.smascaro.trackmixing.search.model.SearchResult
import com.smascaro.trackmixing.search.model.repository.datasource.SearchResultsNetworkDataSource
import javax.inject.Inject

class SearchResultsRepository @Inject constructor(
    private val remoteDataSource: SearchResultsNetworkDataSource
) {
    suspend fun query(query: String): List<SearchResult> {
        //Check cache
        //Api request if not cached
        val intermediate = remoteDataSource.query(query)
        return remoteDataSource.fetchAndMapDetailsFromSearchResponse(intermediate)
    }
}