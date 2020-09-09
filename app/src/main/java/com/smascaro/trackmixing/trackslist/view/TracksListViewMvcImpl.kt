package com.smascaro.trackmixing.trackslist.view

import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.SimpleItemAnimator
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.smascaro.trackmixing.R
import com.smascaro.trackmixing.common.data.model.Track
import com.smascaro.trackmixing.common.view.architecture.BaseObservableViewMvc
import javax.inject.Inject

class TracksListViewMvcImpl @Inject constructor(
    private val tracksListAdapter: TracksListAdapter
) : BaseObservableViewMvc<TracksListViewMvc.Listener>(),
    TracksListAdapter.Listener,
    TracksListViewMvc {

    private lateinit var mRecyclerViewTracks: RecyclerView

    override fun bindRootView(rootView: View?) {
        super.bindRootView(rootView)
        initialize()
    }

    private fun initialize() {
        mRecyclerViewTracks = findViewById(R.id.rvTracks)
        mRecyclerViewTracks.layoutManager = LinearLayoutManager(getContext())
        (mRecyclerViewTracks.itemAnimator as SimpleItemAnimator).supportsChangeAnimations = false
        mRecyclerViewTracks.setHasFixedSize(true)
        tracksListAdapter.setOnTrackClickedListener(this)
        mRecyclerViewTracks.adapter = this.tracksListAdapter
        val fab = findViewById<FloatingActionButton>(R.id.fabTempMode)
        fab.setOnClickListener {
            getListeners().forEach {
                it.onSearchNavigationButtonClicked()
            }
        }

    }

    override fun bindTracks(tracks: List<Track>) {
        this.tracksListAdapter.bindTracks(tracks)
    }

    override fun onTrackClicked(track: Track) {
        getListeners().forEach {
            it.onTrackClicked(track)
        }
    }


}