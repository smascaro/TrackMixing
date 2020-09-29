package com.smascaro.trackmixing.search.view

import android.annotation.SuppressLint
import android.view.View
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.smascaro.trackmixing.R
import com.smascaro.trackmixing.common.utils.ui.UiUtils
import com.smascaro.trackmixing.common.view.architecture.BaseObservableViewMvc
import com.smascaro.trackmixing.main.components.toolbar.view.ObservableQuerySearch.QuerySearchListener
import com.smascaro.trackmixing.main.components.toolbar.view.ToolbarViewMvc
import com.smascaro.trackmixing.player.business.downloadtrack.TrackDownloadService
import com.smascaro.trackmixing.search.model.SearchResult
import javax.inject.Inject
import kotlin.concurrent.thread

class SearchResultsViewMvcImpl @Inject constructor(
    private val searchResultsAdapter: SearchResultsAdapter,
    private val uiUtils: UiUtils,
    private val toolbarViewMvc: ToolbarViewMvc
) : BaseObservableViewMvc<SearchResultsViewMvc.Listener>(),
    SearchResultsAdapter.Listener,
    SearchResultsViewMvc, QuerySearchListener {
    private lateinit var resultsRecyclerView: RecyclerView

    override fun bindRootView(rootView: View?) {
        super.bindRootView(rootView)
        initialize()
    }

    @SuppressLint("ClickableViewAccessibility")
    private fun initialize() {
        resultsRecyclerView = findViewById(R.id.rv_search_results)
        resultsRecyclerView.layoutManager = LinearLayoutManager(getContext())
        searchResultsAdapter.setOnSearchResultClickedListener(this)
        resultsRecyclerView.adapter = searchResultsAdapter

        resultsRecyclerView.setOnTouchListener { v, event ->
            toolbarViewMvc.cleanUp()
            return@setOnTouchListener false
        }
        toolbarViewMvc.registerQuerySearchListener(this)
    }

    private fun hideKeyboard() {
        uiUtils.hideKeyboard(getRootView())
    }

    override fun onSearchResultClicked(searchResult: SearchResult) {
        showMessage("Requesting \"${searchResult.title}\"")
//        searchInputText.clearFocus()
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

    override fun startRequest(url: String) {
        if (getContext() != null) {
            thread {
                TrackDownloadService.start(getContext()!!, url)
            }
        }
    }

    override fun onQuerySearchExecute(query: String) {
        hideKeyboard()
        getListeners().forEach {
            it.onSearchButtonClicked(query)
        }
    }
}