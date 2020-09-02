package com.smascaro.trackmixing.search.view

import android.app.Activity
import android.view.MotionEvent
import android.view.View
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import com.smascaro.trackmixing.R
import com.smascaro.trackmixing.common.view.architecture.BaseObservableViewMvc
import com.smascaro.trackmixing.search.model.SearchResult
import javax.inject.Inject

class SearchResultsViewMvcImpl @Inject constructor(
    private val searchResultsAdapter: SearchResultsAdapter
) : BaseObservableViewMvc<SearchResultsViewMvc.Listener>(),
    SearchResultsAdapter.Listener,
    SearchResultsViewMvc {
    private lateinit var resultsRecyclerView: RecyclerView

    private lateinit var searchInputLayout: TextInputLayout
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

        searchInputLayout = findViewById(R.id.il_search_input_layout)
        searchInputText = findViewById(R.id.et_search_input)
        searchInputText.setOnFocusChangeListener { v, hasFocus ->
            searchInputLayout.setStartIconDrawable(
                if (hasFocus) {
                    R.drawable.ic_back_24dp
                } else {
                    R.drawable.ic_search_24dp
                }
            )
        }

        searchInputText.setOnEditorActionListener { v, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                getListeners().forEach {
                    val queryText = getTextInSearchInput()
                    it.onSearchButtonClicked(queryText)
                }

                hideKeyboard()
                v.clearFocus()
                return@setOnEditorActionListener true
            }
            false
        }

        resultsRecyclerView.setOnTouchListener { v, event ->
            searchInputText.clearFocus()
            return@setOnTouchListener false
        }
        searchInputText.setOnTouchListener { v, event ->
            val DRAWABLE_LEFT_IDX = 0
            val DRAWABLE_TOP_IDX = 1
            val DRAWABLE_RIGHT_IDX = 2
            val DRAWABLE_BOTTOM_IDX = 3
            if (event.action == MotionEvent.ACTION_UP) {
                if (searchInputText.hasFocus()) {
                    if (event.rawX >= (searchInputText.left - searchInputText.compoundDrawables[DRAWABLE_LEFT_IDX].bounds.width())) {
                        hideKeyboard()
                        searchInputText.clearFocus()
                        true
                    }
                }
            }

            false
        }
    }

    private fun hideKeyboard() {
        val imm =
            getContext()?.getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(getRootView().rootView.windowToken, 0)
    }

    private fun getTextInSearchInput(): String {
        return searchInputText.text?.toString() ?: ""
    }

    override fun onSearchResultClicked(searchResult: SearchResult) {
        showMessage("Clicked on \"${searchResult.title}\"")
        searchInputText.clearFocus()
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