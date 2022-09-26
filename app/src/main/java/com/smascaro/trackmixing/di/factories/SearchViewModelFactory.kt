package com.smascaro.trackmixing.di.factories

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.smascaro.trackmixing.search.view.SearchViewModel
import javax.inject.Inject
import javax.inject.Provider

class SearchViewModelFactory @Inject constructor(
    viewModelProvider: Provider<SearchViewModel>
) : ViewModelProvider.Factory {
    private val providers = mapOf<Class<*>, Provider<out ViewModel>>(
        SearchViewModel::class.java to viewModelProvider
    )

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return providers[modelClass]!!.get() as T
    }
}