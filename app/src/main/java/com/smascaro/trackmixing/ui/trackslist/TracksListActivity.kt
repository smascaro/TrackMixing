package com.smascaro.trackmixing.ui.trackslist

import android.os.Bundle
import android.view.LayoutInflater
import com.smascaro.trackmixing.ui.common.BaseActivity

class TracksListActivity : BaseActivity() {
    private lateinit var mTracksListController: TracksListController
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val viewMvc = getCompositionRoot().getViewMvcFactory().getTracksListViewMvc(null)
        mTracksListController = getCompositionRoot().getTracksListController()
        mTracksListController.bindView(viewMvc)
        setContentView(viewMvc.getRootView())

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