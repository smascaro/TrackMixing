package com.smascaro.trackmixing.search.view

import android.annotation.SuppressLint
import android.view.View
import android.view.inputmethod.EditorInfo
import android.widget.EditText
import android.widget.ProgressBar
import android.widget.Toast
import androidx.core.widget.doOnTextChanged
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.smascaro.trackmixing.R
import com.smascaro.trackmixing.common.di.coroutines.MainCoroutineScope
import com.smascaro.trackmixing.common.utils.ui.UiUtils
import com.smascaro.trackmixing.common.view.architecture.BaseObservableViewMvc
import com.smascaro.trackmixing.player.business.downloadtrack.TrackDownloadService
import com.smascaro.trackmixing.search.model.SearchResult
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

class SearchResultsViewMvcImpl @Inject constructor(
    private val searchResultsAdapter: SearchResultsAdapter,
    private val uiUtils: UiUtils,
    private val ui: MainCoroutineScope
) : BaseObservableViewMvc<SearchResultsViewMvc.Listener>(),
    SearchResultsAdapter.Listener,
    SearchResultsViewMvc {
    private lateinit var searchInput: EditText
    private lateinit var clearInputButton: View
    private lateinit var resultsRecyclerView: RecyclerView
    private lateinit var backButton: View
    private lateinit var loadingProgressBar: ProgressBar

    override fun initialize() {
        super.initialize()
        resultsRecyclerView = findViewById(R.id.rv_search_results)
        searchInput = findViewById(R.id.et_toolbar_search_input)
        clearInputButton = findViewById(R.id.iv_toolbar_search_clear)
        backButton = findViewById(R.id.iv_toolbar_back_button)
        loadingProgressBar = findViewById(R.id.pb_search_loading_progress)

        initializeBackButton()
        initializeSearchInput()
        initializeRecyclerView()
        initializeLoadingProgressBar()

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

    private fun initializeLoadingProgressBar() {
        loadingProgressBar.visibility = View.INVISIBLE
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

    override fun showProgressBar() {
        loadingProgressBar.visibility = View.VISIBLE
    }

    override fun hideProgressBar() {
        loadingProgressBar.visibility = View.INVISIBLE
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