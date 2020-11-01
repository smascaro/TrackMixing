package com.smascaro.trackmixing.search.model.repository.datasource

import com.smascaro.trackmixing.R
import com.smascaro.trackmixing.common.utils.ResourcesWrapper
import com.smascaro.trackmixing.search.model.SearchResult
import com.smascaro.trackmixing.search.model.SearchResultResponseSchema
import com.smascaro.trackmixing.search.model.repository.YoutubeApi
import javax.inject.Inject

class SearchResultsNetworkDataSource @Inject constructor(
    private val youtubeApi: YoutubeApi,
    resourcesWrapper: ResourcesWrapper
) {
    private val apiKey = resourcesWrapper.getString(R.string.youtube_api_key)

    suspend fun query(query: String): SearchResultResponseSchema {
        return youtubeApi.search(query, apiKey)
    }

    suspend fun fetchAndMapDetailsFromSearchResponse(searchResultResponseSchema: SearchResultResponseSchema): List<SearchResult> {
        val searchResultIds = searchResultResponseSchema.items.map {
            it.id.videoId
        }.joinToString(separator = ",", prefix = "", postfix = "")
        val details = youtubeApi.fetchDetails(searchResultIds, apiKey)
        val searchResults = mutableListOf<SearchResult>()
        searchResultResponseSchema.items.forEach { searchResult ->
            val found = details.items.find { details ->
                searchResult.id.videoId == details.id
            }
            if (found != null) {
                searchResults.add(
                    SearchResultMapper.map(searchResult, found)
                )
            }
        }
        return searchResults.toList()
    }
}