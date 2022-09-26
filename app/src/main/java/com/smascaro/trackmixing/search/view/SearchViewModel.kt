package com.smascaro.trackmixing.search.view

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.smascaro.trackmixing.search.business.search.SearchYoutubeVideosUseCase
import com.smascaro.trackmixing.search.model.SearchResult
import com.smascaro.trackmixing.utilities.Result
import com.smascaro.trackmixing.utilities.SingleLiveEvent
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

class SearchViewModel @Inject constructor(
    private val searchYoutubeVideosUseCase: SearchYoutubeVideosUseCase
) : ViewModel() {

    private val _searchResults = MutableLiveData<List<SearchResult>>()
    val searchResult: LiveData<List<SearchResult>> = _searchResults

    val onError = SingleLiveEvent<ErrorType>()

    val onProgress = SingleLiveEvent<Boolean>()

    fun search(query: String) {
        viewModelScope.launch(Dispatchers.IO) {
            onProgress.postValue(true)
            when (val searchResult = searchYoutubeVideosUseCase.execute(query)) {
                is Result.Failure -> onError.postValue(ErrorType.SEARCH_FAILED)
                is Result.Success -> _searchResults.postValue(searchResult.result)
            }
            onProgress.postValue(false)
        }
    }

    enum class ErrorType {
        SEARCH_FAILED
    }
}