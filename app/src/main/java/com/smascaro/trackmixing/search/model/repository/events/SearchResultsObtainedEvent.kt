package com.smascaro.trackmixing.search.model.repository.events

import com.smascaro.trackmixing.search.model.SearchResult

sealed class SearchResultsObtainedEvent {
    class Success(val results: List<SearchResult>) : SearchResultsObtainedEvent()
    class Failure(val message: String) : SearchResultsObtainedEvent()
}