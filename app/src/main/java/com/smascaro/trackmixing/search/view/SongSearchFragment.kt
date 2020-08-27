package com.smascaro.trackmixing.search.view

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.smascaro.trackmixing.R
import com.smascaro.trackmixing.main.view.MainActivity
import com.smascaro.trackmixing.search.controller.SearchResultsController
import javax.inject.Inject

class SongSearchFragment : Fragment() {

    @Inject
    lateinit var searchResultsController: SearchResultsController

    @Inject
    lateinit var viewMvc: SearchResultsViewMvc

    override fun onAttach(context: Context) {
        super.onAttach(context)
        (activity as MainActivity).mainComponent.inject(this)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        viewMvc.bindRootView(inflater.inflate(R.layout.fragment_song_search, container, false))
        searchResultsController.bindViewMvc(viewMvc)
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
        searchResultsController.onDestroy()
    }
}
