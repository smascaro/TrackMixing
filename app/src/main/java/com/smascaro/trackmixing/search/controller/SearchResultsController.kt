package com.smascaro.trackmixing.search.controller

import com.smascaro.trackmixing.R
import com.smascaro.trackmixing.common.controller.BaseNavigatorController
import com.smascaro.trackmixing.common.utils.navigation.NavigationHelper
import com.smascaro.trackmixing.common.utils.ResourcesWrapper
import com.smascaro.trackmixing.search.business.SearchYoutubeVideosUseCase
import com.smascaro.trackmixing.search.model.SearchResult
import com.smascaro.trackmixing.search.view.SearchResultsViewMvc
import javax.inject.Inject

class SearchResultsController @Inject constructor(
    private val searchYoutubeVideosUseCase: SearchYoutubeVideosUseCase,
    resourcesWrapper: ResourcesWrapper,
    p_navigationHelper: NavigationHelper
) : BaseNavigatorController<SearchResultsViewMvc>(p_navigationHelper),
    SearchResultsViewMvc.Listener,
    SearchYoutubeVideosUseCase.Listener {
    private val maxVideoDuration =
        resourcesWrapper.getInteger(R.integer.youtube_api_results_max_duration_minutes) * 60

    override fun onSearchButtonClicked(currentText: String) {
        searchYoutubeVideosUseCase.execute(currentText)
        searchYoutubeVideosUseCase.registerListener(this)
    }

    override fun onSearchResultClicked(searchResult: SearchResult) {
        viewMvc.startRequest("https://youtu.be/${searchResult.videoId}")
        navigationHelper.back()
    }

    fun onStart() {
        viewMvc.registerListener(this)
    }

    fun onStop() {
        searchYoutubeVideosUseCase.unregisterListener(this)
        viewMvc.unregisterListener(this)
    }

    fun onDestroy() {
        searchYoutubeVideosUseCase.unregisterListener(this)
        viewMvc.unregisterListener(this)
    }

    override fun onResultsReady(results: List<SearchResult>) {
        val filteredResults = results.filter {
            it.secondsLong < maxVideoDuration
        }
        viewMvc.bindResults(filteredResults)
    }

    override fun onErrorOccurred(message: String) {
        viewMvc.showMessage(message)
    }

}