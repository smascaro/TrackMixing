package com.smascaro.trackmixing.trackslist.view

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.interpolator.view.animation.FastOutSlowInInterpolator
import androidx.navigation.fragment.findNavController
import com.google.android.material.transition.MaterialContainerTransform
import com.google.android.material.transition.MaterialFadeThrough
import com.smascaro.trackmixing.R
import com.smascaro.trackmixing.common.view.ui.BaseFragment
import com.smascaro.trackmixing.main.view.MainActivity
import com.smascaro.trackmixing.trackslist.controller.TracksListController
import javax.inject.Inject

class TracksListFragment : BaseFragment() {

    @Inject
    lateinit var mTracksListController: TracksListController


    @Inject
    lateinit var viewMvc: TracksListViewMvc

    override fun onAttach(context: Context) {
        super.onAttach(context)

        (activity as MainActivity).mainComponent.inject(this)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enterTransition = MaterialFadeThrough()
        exitTransition = MaterialContainerTransform().apply {
            duration = 375
            interpolator = FastOutSlowInInterpolator()
            startDelay = 25
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        viewMvc.bindRootView(inflater.inflate(R.layout.fragment_tracks_list, null, false))
        mTracksListController.bindViewMvc(viewMvc)
        mTracksListController.bindNavController(findNavController())
        return viewMvc.getRootView()
    }

    override fun onStart() {
        super.onStart()
        mTracksListController.onStart()
    }

    override fun onStop() {
        super.onStop()
        mTracksListController.onStop()
    }
}


