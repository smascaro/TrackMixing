package com.smascaro.trackmixing.player.view

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.android.material.transition.MaterialSharedAxis
import com.smascaro.trackmixing.base.ui.BaseFragment
import com.smascaro.trackmixing.player.R
import com.smascaro.trackmixing.player.di.PlayerComponentProvider
import javax.inject.Inject

class PlayerFragment : BaseFragment() {
    @Inject
    lateinit var playerController: com.smascaro.trackmixing.player.controller.TrackPlayerController

    @Inject
    lateinit var viewMvc: TrackPlayerViewMvc

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enterTransition = MaterialSharedAxis(MaterialSharedAxis.Y, true).apply {
            duration = 200
        }
        returnTransition = MaterialSharedAxis(MaterialSharedAxis.Y, false).apply {
            duration = 200
        }
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        (requireActivity().application as PlayerComponentProvider).providePlayerComponent().inject(this)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        super.onCreateView(inflater, container, savedInstanceState)
        viewMvc.bindRootView(inflater.inflate(R.layout.fragment_player, container, false))
        playerController.bindViewMvc(viewMvc)
        return viewMvc.getRootView()
    }

    override fun onStart() {
        super.onStart()
        playerController.onStart()
    }

    override fun onStop() {
        super.onStop()
        playerController.onStop()
    }
}