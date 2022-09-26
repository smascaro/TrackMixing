package com.smascaro.trackmixing.search.business.search

import com.smascaro.trackmixing.search.model.SearchResult
import com.smascaro.trackmixing.search.model.repository.SearchResultsRepository
import com.smascaro.trackmixing.utilities.Result
import com.smascaro.trackmixing.utilities.failure
import com.smascaro.trackmixing.utilities.success
import timber.log.Timber
import javax.inject.Inject

class SearchYoutubeVideosUseCase @Inject constructor(private val searchResultsRepository: SearchResultsRepository) {
    suspend fun execute(query: String): Result<List<SearchResult>, Throwable> {
        return try {
            val results =
                searchResultsRepository.query(query).filter { it.secondsLong <= MAX_VIDEO_DURATION_SECONDS }
            success(results)
        } catch (e: Exception) {
            Timber.e(e)
            failure(e)
        }
    }

    companion object {
        const val MAX_VIDEO_DURATION_SECONDS = 12 * 60
    }
}