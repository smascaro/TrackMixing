package com.smascaro.trackmixing.search.view

import android.view.View
import android.widget.ImageView
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.textfield.TextInputEditText
import com.smascaro.trackmixing.R
import com.smascaro.trackmixing.common.view.architecture.BaseObservableViewMvc
import com.smascaro.trackmixing.search.model.SearchResult
import timber.log.Timber
import javax.inject.Inject

class SearchResultsViewMvcImpl @Inject constructor(
    private val searchResultsAdapter: SearchResultsAdapter
) : BaseObservableViewMvc<SearchResultsViewMvc.Listener>(),
    SearchResultsAdapter.Listener,
    SearchResultsViewMvc {
    private lateinit var resultsRecyclerView: RecyclerView
    private lateinit var searchButton: ImageView
    private lateinit var searchInputText: TextInputEditText

    override fun bindRootView(rootView: View?) {
        super.bindRootView(rootView)
        initialize()
    }

    private fun initialize() {
        resultsRecyclerView = findViewById(R.id.rv_search_results)
        resultsRecyclerView.layoutManager = LinearLayoutManager(getContext())
        searchResultsAdapter.setOnSearchResultClickedListener(this)
        resultsRecyclerView.adapter = searchResultsAdapter

        searchInputText = findViewById(R.id.et_search_input)

        searchButton = findViewById(R.id.iv_search_button)
        searchButton.setOnClickListener {
            val queryText = searchInputText.text.toString()
            if (queryText != null) {
                Timber.d("Searching with keyword $queryText")
                getListeners().forEach {
                    it.onSearchButtonClicked(queryText)
                }
            }
        }
    }

    override fun onSearchResultClicked(searchResult: SearchResult) {
        getListeners().forEach {
            it.onSearchResultClicked(searchResult)
        }
    }

    override fun bindResults(results: List<SearchResult>) {
        searchResultsAdapter.bindResults(results)
    }

    override fun showMessage(message: String) {
        Toast.makeText(getContext(), message, Toast.LENGTH_LONG).show()
    }

}