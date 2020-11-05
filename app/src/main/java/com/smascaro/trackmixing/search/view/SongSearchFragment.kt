package com.smascaro.trackmixing.search.view

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import com.google.android.material.transition.MaterialSharedAxis
import com.smascaro.trackmixing.R
import com.smascaro.trackmixing.common.di.search.SearchComponentProvider
import com.smascaro.trackmixing.common.view.ui.BaseFragment
import com.smascaro.trackmixing.search.controller.SearchResultsController
import javax.inject.Inject

class SongSearchFragment : BaseFragment() {
    @Inject
    lateinit var searchResultsController: SearchResultsController

    @Inject
    lateinit var viewMvc: SearchResultsViewMvc

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
        // (activity as MainActivity).mainComponent.inject(this)
        // (activity.application as TrackMixingApplication).appComponent

        // DaggerSearchComponent.builder()
        //     .withBaseComponent(BaseApplication.baseComponent)
        //     .build()
        //     .inject(this)
        (requireActivity().application as SearchComponentProvider).provideSearchComponent().inject(this)
    }

    override fun getFragmentTitle(): String {
        return "Search a song"
    }

    override fun isBackNavigable() = true

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        super.onCreateView(inflater, container, savedInstanceState)
        viewMvc.bindRootView(inflater.inflate(R.layout.fragment_song_search, container, false))
        searchResultsController.bindViewMvc(viewMvc)
        searchResultsController.bindNavController(findNavController())
        return viewMvc.getRootView()
    }

    override fun onStart() {
        super.onStart()
        searchResultsController.onStart()
    }

    override fun onStop() {
        super.onStop()
        searchResultsController.onStop()
    }

    override fun onDestroy() {
        super.onDestroy()
        searchResultsController.dispose()
    }
}
