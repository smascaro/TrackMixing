package com.smascaro.trackmixing.ui.trackslist

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.app.SharedElementCallback
import androidx.fragment.app.Fragment
import androidx.interpolator.view.animation.FastOutSlowInInterpolator
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.transition.MaterialContainerTransform
import com.google.android.material.transition.MaterialFadeThrough
import com.smascaro.trackmixing.R
import com.smascaro.trackmixing.ui.common.BaseFragment

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [TracksListFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class TracksListFragment : BaseFragment() {

    private lateinit var mTracksListController: TracksListController

    init {
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
        val viewMvc = getCompositionRoot().getViewMvcFactory().getTracksListViewMvc(null)
        viewMvc.bindNavigationHelper(
            getCompositionRoot().getNavigationHelper(
                getNavigationController()
            )
        )
        mTracksListController =
            getCompositionRoot().getTracksListController(getNavigationController())
        mTracksListController.bindView(viewMvc)

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


