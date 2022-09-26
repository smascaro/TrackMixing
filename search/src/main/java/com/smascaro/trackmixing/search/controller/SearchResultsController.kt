package com.smascaro.trackmixing.search.controller

import com.smascaro.trackmixing.base.coroutine.IoCoroutineScope
import com.smascaro.trackmixing.base.coroutine.MainCoroutineScope
import com.smascaro.trackmixing.base.utils.ResourcesWrapper
import com.smascaro.trackmixing.base.utils.navigation.BaseNavigatorController
import com.smascaro.trackmixing.base.utils.navigation.NavigationHelper
import com.smascaro.trackmixing.search.R
import com.smascaro.trackmixing.search.business.search.SearchYoutubeVideosUseCase
import com.smascaro.trackmixing.search.model.SearchResult
import com.smascaro.trackmixing.search.view.SearchResultsViewMvc
import kotlinx.coroutines.launch
import javax.inject.Inject

class SearchResultsController @Inject constructor(
    private val searchYoutubeVideosUseCase: SearchYoutubeVideosUseCase,
    private val io: IoCoroutineScope,
    private val ui: MainCoroutineScope,
    resourcesWrapper: ResourcesWrapper,
    navigationHelper: NavigationHelper
) : BaseNavigatorController<SearchResultsViewMvc>(navigationHelper),
    SearchResultsViewMvc.Listener {
    private val maxVideoDuration =
        resourcesWrapper.getInteger(R.integer.youtube_api_results_max_duration_minutes) * 60

    override fun onSearchButtonClicked(currentText: String) {
        viewMvc.showProgress()
        viewMvc.bindResults(listOf())
        io.launch {
            val useCaseResult = searchYoutubeVideosUseCase.execute(currentText)
            ui.launch {
                when (useCaseResult) {
                    is SearchYoutubeVideosUseCase.Result.Success -> handleSearchResults(
                        useCaseResult.searchResults
                    )
                    is SearchYoutubeVideosUseCase.Result.Failure -> handleSearchError(useCaseResult.error)
                }
                viewMvc.hideProgress()
            }
        }
    }

    override fun onSearchResultClicked(searchResult: SearchResult) {
        viewMvc.startRequest("https://youtu.be/${searchResult.videoId}")
        navigationHelper.back()
    }

    override fun onBackButtonPressed() {
        viewMvc.cleanUp()
        navigationHelper.back()
    }

    fun onStart() {
        viewMvc.registerListener(this)
    }

    fun onStop() {
        dispose()
    }

    private fun handleSearchResults(results: List<SearchResult>) {
        val filteredResults = results.filter {
            it.secondsLong < maxVideoDuration
        }
        viewMvc.bindResults(filteredResults)
    }

    private fun handleSearchError(error: Throwable) {
        viewMvc.showMessage(error.localizedMessage ?: "Unknown error")
    }

    override fun dispose() {
        viewMvc.unregisterListener(this)
    }
}