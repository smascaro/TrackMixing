package com.smascaro.trackmixing.trackslist.view

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.SimpleItemAnimator
import com.google.android.material.transition.MaterialFadeThrough
import com.google.android.material.transition.MaterialSharedAxis
import com.smascaro.trackmixing.base.ui.BaseFragment
import com.smascaro.trackmixing.databinding.FragmentTracksListBinding
import com.smascaro.trackmixing.di.ViewModelProviderFactory
import com.smascaro.trackmixing.trackslist.components.toolbar.controller.ToolbarController
import com.smascaro.trackmixing.trackslist.components.toolbar.view.ToolbarViewMvc
import com.smascaro.trackmixing.trackslist.controller.TracksListViewModel
import com.smascaro.trackmixing.utilities.nullifyOnDestroy
import javax.inject.Inject

class TracksListFragment : BaseFragment() {
    private var binding: FragmentTracksListBinding by nullifyOnDestroy()

    private lateinit var viewModel: TracksListViewModel

    @Inject
    lateinit var providerFactory: ViewModelProviderFactory

    @Inject
    lateinit var toolbarController: ToolbarController

    @Inject
    lateinit var toolbarViewMvc: ToolbarViewMvc

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
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        super.onCreateView(inflater, container, savedInstanceState)
        binding = FragmentTracksListBinding.inflate(inflater, container, false)
        toolbarViewMvc.bindRootView(binding.root)
        toolbarController.bindViewMvc(toolbarViewMvc)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel = ViewModelProvider(viewModelStore, providerFactory)[TracksListViewModel::class.java]
        initializeRecyclerView()
        setupObservers()
    }

    private fun initializeRecyclerView() {
        val layoutManagerWrapper = object : LinearLayoutManager(context) {
            override fun supportsPredictiveItemAnimations(): Boolean {
                return false
            }
        }
        binding.rvTracks.layoutManager = layoutManagerWrapper
        (binding.rvTracks.itemAnimator as SimpleItemAnimator).supportsChangeAnimations = false
        binding.rvTracks.setHasFixedSize(true)

        binding.rvTracks.adapter = TracksListAdapter().apply {
            setOnTrackClickedListener(viewModel::onTrackClicked)
        }
    }

    private fun setupObservers() {
        viewModel.tracks.observe(viewLifecycleOwner) { tracks ->
            (binding.rvTracks.adapter as? TracksListAdapter)?.bindTracks(tracks)
        }
    }

    override fun onStart() {
        super.onStart()
        viewModel.onStart()
        toolbarController.bindNavController(findNavController())
        toolbarController.onStart()
    }

    override fun onStop() {
        super.onStop()
        viewModel.onStop()
        toolbarController.onStop()
    }

    override fun onDestroy() {
        super.onDestroy()
        toolbarController.dispose()
    }

    fun beforeNavigationToSearch() {
        exitTransition = MaterialSharedAxis(MaterialSharedAxis.Z, true).apply {
            duration = 200
        }
        reenterTransition = MaterialSharedAxis(MaterialSharedAxis.Z, false).apply {
            duration = 200
        }
    }
}


