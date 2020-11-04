package com.smascaro.trackmixing.search.controller

import com.smascaro.trackmixing.R
import com.smascaro.trackmixing.common.controller.BaseNavigatorController
import com.smascaro.trackmixing.common.di.coroutines.IoCoroutineScope
import com.smascaro.trackmixing.common.di.coroutines.MainCoroutineScope
import com.smascaro.trackmixing.common.utils.ResourcesWrapper
import com.smascaro.trackmixing.common.utils.navigation.NavigationHelper
import com.smascaro.trackmixing.search.business.SearchYoutubeVideosUseCase
import com.smascaro.trackmixing.search.model.SearchResult
import com.smascaro.trackmixing.search.view.SearchResultsViewMvc
import kotlinx.coroutines.launch
import javax.inject.Inject

class SearchResultsController @Inject constructor(
    private val searchYoutubeVideosUseCase: SearchYoutubeVideosUseCase,
    private val io: IoCoroutineScope,
    private val ui: MainCoroutineScope,
    resourcesWrapper: ResourcesWrapper,
    p_navigationHelper: NavigationHelper
) : BaseNavigatorController<SearchResultsViewMvc>(p_navigationHelper),
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