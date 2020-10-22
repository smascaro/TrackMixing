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
import com.google.android.material.transition.MaterialSharedAxis
import com.smascaro.trackmixing.R
import com.smascaro.trackmixing.common.view.ui.BaseFragment
import com.smascaro.trackmixing.main.view.MainActivity
import com.smascaro.trackmixing.trackslist.controller.TracksListController
import javax.inject.Inject

class TracksListFragment : BaseFragment(), TracksListController.NavigationListener {
    @Inject
    lateinit var mTracksListController: TracksListController

    @Inject
    lateinit var viewMvc: TracksListViewMvc

    override fun onAttach(context: Context) {
        super.onAttach(context)

        (activity as MainActivity).mainComponent.inject(this)
    }

    override fun getFragmentTitle(): String {
        return "My studio"
    }

    override fun isBackNavigable() = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enterTransition = MaterialFadeThrough().apply {
            duration=300
        }
        reenterTransition = MaterialFadeThrough().apply {
            duration=300
        }
        exitTransition = MaterialFadeThrough().apply {
            duration = 300
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        super.onCreateView(inflater, container, savedInstanceState)
        viewMvc.bindRootView(inflater.inflate(R.layout.fragment_tracks_list, null, false))
        mTracksListController.bindViewMvc(viewMvc)
        mTracksListController.bindNavController(findNavController())
        return viewMvc.getRootView()
    }

    override fun onStart() {
        super.onStart()
        mTracksListController.onStart()
        mTracksListController.registerNavigationListener(this)
    }

    override fun onStop() {
        super.onStop()
        mTracksListController.onStop()
        mTracksListController.unregisterNavigationListener()
    }

    override fun onDestroy() {
        super.onDestroy()
        mTracksListController.dispose()
    }

    override fun beforeNavigationToSearch() {
        exitTransition=MaterialSharedAxis(MaterialSharedAxis.Z, true).apply {
            duration=200
        }
        reenterTransition=MaterialSharedAxis(MaterialSharedAxis.Z, false).apply {
            duration=200
        }
    }
}


