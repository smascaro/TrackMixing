package com.smascaro.trackmixing.ui.trackslist

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.view.menu.MenuView
import androidx.fragment.app.Fragment
import androidx.navigation.Navigation.findNavController
import com.smascaro.trackmixing.R
import com.smascaro.trackmixing.ui.common.BaseFragment
import com.smascaro.trackmixing.ui.common.navigationhelper.NavigationHelper

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
    private lateinit var mNavigationHelper: NavigationHelper

    init {
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val viewMvc = getCompositionRoot().getViewMvcFactory().getTracksListViewMvc(null)
        mTracksListController = getCompositionRoot().getTracksListController()
        mTracksListController.bindView(viewMvc)
        mNavigationHelper = getCompositionRoot().getNavigationHelper()
        val navController = findNavController(requireActivity(), R.id.nav_host_fragment)
        mNavigationHelper.bindNavController(navController)
        viewMvc.bindNavigationHelper(mNavigationHelper)
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


