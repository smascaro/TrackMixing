package com.smascaro.trackmixing.search.view

import android.annotation.SuppressLint
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.widget.EditText
import android.widget.Toast
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.facebook.shimmer.ShimmerFrameLayout
import com.google.android.material.transition.MaterialSharedAxis
import com.smascaro.trackmixing.R
import com.smascaro.trackmixing.base.ui.BaseFragment
import com.smascaro.trackmixing.base.ui.getSearchComponent
import com.smascaro.trackmixing.base.utils.KeyboardUtils
import com.smascaro.trackmixing.di.component.SearchComponentProvider
import com.smascaro.trackmixing.search.business.download.TrackDownloadService

class SongSearchFragment : BaseFragment() {
    private val viewModel: SearchViewModel by viewModels {
        getSearchComponent().viewModelsFactory()
    }

    private lateinit var searchInput: EditText
    private lateinit var clearInputButton: View
    private lateinit var resultsRecyclerView: RecyclerView
    private lateinit var backButton: View
    private lateinit var shimmerContainer: ShimmerFrameLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enterTransition = MaterialSharedAxis(MaterialSharedAxis.Z, true).apply {
            duration = 200
        }
        returnTransition = MaterialSharedAxis(MaterialSharedAxis.Z, false).apply {
            duration = 200
        }
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        (requireActivity().application as SearchComponentProvider).provideSearchComponent().inject(this)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        super.onCreateView(inflater, container, savedInstanceState)
        return inflater.inflate(R.layout.fragment_song_search, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initializeViews(view)
        setupObservers()
    }

    private fun setupObservers() {
        viewModel.searchResult.observe(viewLifecycleOwner) { searchResults ->
            clearKeyboard()
            (resultsRecyclerView.adapter as? SearchResultsAdapter)?.bindResults(searchResults)
        }
        viewModel.onError.observe(viewLifecycleOwner) {
            clearKeyboard()
            showMessage(
                when (it) {
                    SearchViewModel.ErrorType.SEARCH_FAILED -> "Something went wrong with the search. Please try again."
                }
            )
        }
        viewModel.onProgress.observe(viewLifecycleOwner) { progress ->
            if (progress) {
                shimmerContainer.visibility = View.VISIBLE
                shimmerContainer.startShimmer()
            } else {
                shimmerContainer.visibility = View.GONE
                shimmerContainer.stopShimmer()
            }
        }
    }

    private fun initializeViews(fragment: View) {
        resultsRecyclerView = fragment.findViewById(R.id.rv_search_results)
        searchInput = fragment.findViewById(R.id.et_toolbar_search_input)
        clearInputButton = fragment.findViewById(R.id.iv_toolbar_search_clear)
        backButton = fragment.findViewById(R.id.iv_toolbar_back_button)
        shimmerContainer = fragment.findViewById(R.id.shimmer_container)

        initializeBackButton()
        initializeSearchInput()
        initializeRecyclerView()

        focusSearchInput()
    }

    private fun initializeBackButton() {
        backButton.setOnClickListener {
            navigationHelper.back()
        }
    }

    private fun initializeSearchInput() {
        clearInputButton.setOnClickListener {
            searchInput.setText("")
        }
        searchInput.setOnEditorActionListener { _, actionId, event ->
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                onSearchClicked()
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
        val adapter = SearchResultsAdapter()
        adapter.setOnSearchResultClickedListener { searchResult ->
            clearKeyboard()
            showMessage(searchResult.title)
            TrackDownloadService.start(requireContext(), "https://youtu.be/${searchResult.videoId}")
            navigationHelper.back()
        }
        resultsRecyclerView.adapter = adapter

        resultsRecyclerView.setOnTouchListener { _, _ ->
            clearKeyboard()
            return@setOnTouchListener false
        }
    }

    private fun clearKeyboard() {
        searchInput.clearFocus()
        KeyboardUtils.hide(searchInput)
    }

    private fun showMessage(message: String) {
        Toast.makeText(context, "Requesting \"$message\"", Toast.LENGTH_LONG).show()
    }

    private fun focusSearchInput() {
        KeyboardUtils.show(searchInput)
    }

    private fun onSearchClicked() {
        viewModel.search(searchInput.text.toString())
    }
}
