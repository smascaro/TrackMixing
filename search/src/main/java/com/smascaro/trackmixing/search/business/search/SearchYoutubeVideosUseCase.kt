package com.smascaro.trackmixing.search.business.search

import com.smascaro.trackmixing.search.model.SearchResult
import com.smascaro.trackmixing.search.model.repository.SearchResultsRepository
import timber.log.Timber
import javax.inject.Inject

class SearchYoutubeVideosUseCase @Inject constructor(private val searchResultsRepository: SearchResultsRepository) {
    sealed class Result {
        data class Success(val searchResults: List<SearchResult>) : Result()
        data class Failure(val error: Throwable) : Result()
    }

    suspend fun execute(query: String): Result {
        return try {
            val results = searchResultsRepository.query(query)
            Result.Success(results)
        } catch (e: Exception) {
            Timber.e(e)
            Result.Failure(e)
        }
    }
}