package com.smascaro.trackmixing.trackslist.view

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import com.google.android.material.transition.MaterialFadeThrough
import com.google.android.material.transition.MaterialSharedAxis
import com.smascaro.trackmixing.R
import com.smascaro.trackmixing.common.di.main.MainComponentProvider
import com.smascaro.trackmixing.common.view.ui.BaseFragment
import com.smascaro.trackmixing.trackslist.components.toolbar.controller.ToolbarController
import com.smascaro.trackmixing.trackslist.components.toolbar.view.ToolbarViewMvc
import com.smascaro.trackmixing.trackslist.controller.TracksListController
import javax.inject.Inject

class TracksListFragment : BaseFragment(), TracksListController.NavigationListener {
    @Inject
    lateinit var tracksListController: TracksListController

    @Inject
    lateinit var viewMvc: TracksListViewMvc

    @Inject
    lateinit var toolbarController: ToolbarController

    @Inject
    lateinit var toolbarViewMvc: ToolbarViewMvc
    override fun onAttach(context: Context) {
        super.onAttach(context)
        (requireActivity().application as MainComponentProvider).provideMainComponent().inject(this)
    }

    override fun getFragmentTitle(): String {
        return "My studio"
    }

    override fun isBackNavigable() = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enterTransition = MaterialFadeThrough().apply {
            duration = 300
        }
        reenterTransition = MaterialFadeThrough().apply {
            duration = 300
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


        toolbarViewMvc.bindRootView(viewMvc.getRootView())
        toolbarController.bindViewMvc(toolbarViewMvc)

        tracksListController.bindViewMvc(viewMvc)
        tracksListController.bindNavController(findNavController())
        return viewMvc.getRootView()
    }

    override fun onStart() {
        super.onStart()
        tracksListController.onStart()
        tracksListController.registerNavigationListener(this)
        toolbarController.bindNavController(findNavController())
        toolbarController.onStart()
    }

    override fun onStop() {
        super.onStop()
        tracksListController.onStop()
        tracksListController.unregisterNavigationListener()
        toolbarController.onStop()
    }

    override fun onDestroy() {
        super.onDestroy()
        tracksListController.dispose()
        toolbarController.dispose()
    }

    override fun beforeNavigationToSearch() {
        exitTransition = MaterialSharedAxis(MaterialSharedAxis.Z, true).apply {
            duration = 200
        }
        reenterTransition = MaterialSharedAxis(MaterialSharedAxis.Z, false).apply {
            duration = 200
        }
    }
}


