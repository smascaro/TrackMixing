package com.smascaro.trackmixing.search.view

import android.annotation.SuppressLint
import android.view.View
import android.view.inputmethod.EditorInfo
import android.widget.EditText
import android.widget.Toast
import androidx.core.widget.doOnTextChanged
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.facebook.shimmer.ShimmerFrameLayout
import com.smascaro.trackmixing.base.ui.architecture.view.BaseObservableViewMvc
import com.smascaro.trackmixing.base.utils.UiUtils
import com.smascaro.trackmixing.search.R
import com.smascaro.trackmixing.search.business.download.TrackDownloadService
import com.smascaro.trackmixing.search.model.SearchResult
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

class SearchResultsViewMvcImpl @Inject constructor(
    private val searchResultsAdapter: SearchResultsAdapter,
    private val uiUtils: UiUtils
) : BaseObservableViewMvc<SearchResultsViewMvc.Listener>(),
    SearchResultsAdapter.Listener,
    SearchResultsViewMvc {
    private lateinit var searchInput: EditText
    private lateinit var clearInputButton: View
    private lateinit var resultsRecyclerView: RecyclerView
    private lateinit var backButton: View
    private lateinit var shimmerContainer: ShimmerFrameLayout

    override fun initialize() {
        super.initialize()
        resultsRecyclerView = findViewById(R.id.rv_search_results)
        searchInput = findViewById(R.id.et_toolbar_search_input)
        clearInputButton = findViewById(R.id.iv_toolbar_search_clear)
        backButton = findViewById(R.id.iv_toolbar_back_button)
        shimmerContainer = findViewById(R.id.shimmer_container)

        initializeBackButton()
        initializeSearchInput()
        initializeRecyclerView()

        focusSearchInput()
    }

    private fun initializeBackButton() {
        backButton.setOnClickListener {
            getListeners().forEach { it.onBackButtonPressed() }
        }
    }

    private fun initializeSearchInput() {
        clearInputButton.setOnClickListener {
            searchInput.setText("")
        }
        searchInput.setOnEditorActionListener { v, actionId, event ->
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                handleQuerySearch(v.text.toString())
                true
            } else false
        }
        searchInput.doOnTextChanged { text, _, _, _ ->
            text?.let {
                handleQueryTextChange(it.toString())
            }
        }
    }

    private fun handleQueryTextChange(it: String) {
        if (it.isNotEmpty() && clearInputButton.visibility != View.VISIBLE) {
            clearInputButton.visibility = View.VISIBLE
        } else if (it.isEmpty() && clearInputButton.visibility == View.VISIBLE) {
            clearInputButton.visibility = View.INVISIBLE
        }
    }

    @SuppressLint("ClickableViewAccessibility")
    private fun initializeRecyclerView() {
        resultsRecyclerView.layoutManager = LinearLayoutManager(getContext())
        searchResultsAdapter.setOnSearchResultClickedListener(this)
        resultsRecyclerView.adapter = searchResultsAdapter

        resultsRecyclerView.setOnTouchListener { _, _ ->
            cleanUp()
            return@setOnTouchListener false
        }
    }

    private fun focusSearchInput() {
        uiUtils.showKeyboard()
        searchInput.requestFocus()
    }

    override fun onSearchResultClicked(searchResult: SearchResult) {
        showMessage("Requesting \"${searchResult.title}\"")
        getListeners().forEach {
            it.onSearchResultClicked(searchResult)
        }
    }

    override fun showProgress() {
        shimmerContainer.visibility = View.VISIBLE
        shimmerContainer.startShimmer()
    }

    override fun hideProgress() {
        shimmerContainer.visibility = View.GONE
        shimmerContainer.stopShimmer()
    }

    override fun bindResults(results: List<SearchResult>) {
        searchResultsAdapter.bindResults(results)
    }

    override fun showMessage(message: String) {
        Toast.makeText(getContext(), message, Toast.LENGTH_LONG).show()
    }

    override fun startRequest(url: String) {
        if (getContext() != null) {
            CoroutineScope(Dispatchers.Main).launch {
                TrackDownloadService.start(getContext()!!, url)
            }
        }
    }

    private fun handleQuerySearch(query: String) {
        cleanUp()
        getListeners().forEach {
            it.onSearchButtonClicked(query)
        }
    }

    override fun cleanUp() {
        searchInput.clearFocus()
        uiUtils.hideKeyboard(getRootView())
    }
}