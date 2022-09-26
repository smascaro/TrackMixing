package com.smascaro.trackmixing.search.view

import android.annotation.SuppressLint
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.widget.Toast
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.transition.MaterialSharedAxis
import com.smascaro.trackmixing.base.ui.BaseFragment
import com.smascaro.trackmixing.base.ui.getSearchComponent
import com.smascaro.trackmixing.base.utils.KeyboardUtils
import com.smascaro.trackmixing.databinding.FragmentSongSearchBinding
import com.smascaro.trackmixing.di.component.SearchComponentProvider
import com.smascaro.trackmixing.search.business.download.TrackDownloadService
import com.smascaro.trackmixing.utilities.nullifyOnDestroy

class SongSearchFragment : BaseFragment() {
    private var binding: FragmentSongSearchBinding by nullifyOnDestroy()

    private val viewModel: SearchViewModel by viewModels {
        getSearchComponent().viewModelsFactory()
    }

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
    ): View {
        super.onCreateView(inflater, container, savedInstanceState)
        binding = FragmentSongSearchBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initializeBindings()
        setupObservers()
    }

    private fun setupObservers() {
        viewModel.searchResult.observe(viewLifecycleOwner) { searchResults ->
            clearKeyboard()
            (binding.rvSearchResults.adapter as? SearchResultsAdapter)?.bindResults(searchResults)
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
                binding.shimmerContainer.visibility = View.VISIBLE
                binding.shimmerContainer.startShimmer()
            } else {
                binding.shimmerContainer.visibility = View.GONE
                binding.shimmerContainer.stopShimmer()
            }
        }
    }

    private fun initializeBindings() {
        initializeBackButton()
        initializeSearchInput()
        initializeRecyclerView()

        focusSearchInput()
    }

    private fun initializeBackButton() {
        binding.ivToolbarBackButton.setOnClickListener {
            navigationHelper.back()
        }
    }

    private fun initializeSearchInput() {
        binding.ivToolbarSearchClear.setOnClickListener {
            binding.etToolbarSearchInput.setText("")
        }
        binding.etToolbarSearchInput.setOnEditorActionListener { _, actionId, event ->
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                onSearchClicked()
                true
            } else false
        }
        binding.etToolbarSearchInput.doOnTextChanged { text, _, _, _ ->
            text?.let {
                handleQueryTextChange(it.toString())
            }
        }
    }

    private fun handleQueryTextChange(it: String) {
        if (it.isNotEmpty() && binding.ivToolbarSearchClear.visibility != View.VISIBLE) {
            binding.ivToolbarSearchClear.visibility = View.VISIBLE
        } else if (it.isEmpty() && binding.ivToolbarSearchClear.visibility == View.VISIBLE) {
            binding.ivToolbarSearchClear.visibility = View.INVISIBLE
        }
    }

    @SuppressLint("ClickableViewAccessibility")
    private fun initializeRecyclerView() {
        binding.rvSearchResults.layoutManager = LinearLayoutManager(getContext())
        val adapter = SearchResultsAdapter()
        adapter.setOnSearchResultClickedListener { searchResult ->
            clearKeyboard()
            showMessage(searchResult.title)
            TrackDownloadService.start(requireContext(), "https://youtu.be/${searchResult.videoId}")
            navigationHelper.back()
        }
        binding.rvSearchResults.adapter = adapter

        binding.rvSearchResults.setOnTouchListener { _, _ ->
            clearKeyboard()
            return@setOnTouchListener false
        }
    }

    private fun clearKeyboard() {
        binding.etToolbarSearchInput.clearFocus()
        KeyboardUtils.hide(binding)
    }

    private fun showMessage(message: String) {
        Toast.makeText(context, "Requesting \"$message\"", Toast.LENGTH_LONG).show()
    }

    private fun focusSearchInput() {
        KeyboardUtils.show(binding)
    }

    private fun onSearchClicked() {
        viewModel.search(binding.etToolbarSearchInput.text.toString())
    }
}
