package com.smascaro.trackmixing.search.business

import com.smascaro.trackmixing.common.view.architecture.BaseObservable
import com.smascaro.trackmixing.search.model.SearchResult
import com.smascaro.trackmixing.search.model.repository.SearchResultsRepository
import com.smascaro.trackmixing.search.model.repository.events.SearchResultsObtainedEvent
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import javax.inject.Inject

class SearchYoutubeVideosUseCase @Inject constructor(private val searchResultsRepository: SearchResultsRepository) :
    BaseObservable<SearchYoutubeVideosUseCase.Listener>() {
    interface Listener {
        fun onResultsReady(results: List<SearchResult>)
        fun onErrorOccurred(message: String)
    }

    fun execute(query: String) {
        if (!EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().register(this)
        }
        searchResultsRepository.query(query)
    }

    @Subscribe(threadMode = ThreadMode.BACKGROUND)
    fun onMessageEvent(searchResultsObtainedEvent: SearchResultsObtainedEvent) {
        when (searchResultsObtainedEvent) {
            is SearchResultsObtainedEvent.Success -> handleSearchResults(searchResultsObtainedEvent.results)
            is SearchResultsObtainedEvent.Failure -> handleError(searchResultsObtainedEvent.message)
        }
    }

    private fun handleSearchResults(results: List<SearchResult>) {
        getListeners().forEach {
            it.onResultsReady(results)
        }
    }

    private fun handleError(message: String) {
        getListeners().forEach {
            it.onErrorOccurred(message)
        }
    }
}