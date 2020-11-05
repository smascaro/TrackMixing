package com.smascaro.trackmixing.trackslist.view

import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.SimpleItemAnimator
import com.smascaro.trackmixing.R
import com.smascaro.trackmixing.base.data.model.Track
import com.smascaro.trackmixing.common.view.architecture.BaseObservableViewMvc
import javax.inject.Inject

class TracksListViewMvcImpl @Inject constructor(
    private val tracksListAdapter: TracksListAdapter
) : BaseObservableViewMvc<TracksListViewMvc.Listener>(),
    TracksListAdapter.Listener,
    TracksListViewMvc {
    private lateinit var mRecyclerViewTracks: RecyclerView

    override fun initialize() {
        super.initialize()
        mRecyclerViewTracks = findViewById(R.id.rvTracks)
        initializeRecyclerView()
    }

    private fun initializeRecyclerView() {
        val layoutManagerWrapper = object : LinearLayoutManager(getContext()) {
            override fun supportsPredictiveItemAnimations(): Boolean {
                return false
            }
        }
        mRecyclerViewTracks.layoutManager = layoutManagerWrapper
        (mRecyclerViewTracks.itemAnimator as SimpleItemAnimator).supportsChangeAnimations = false
        mRecyclerViewTracks.setHasFixedSize(true)
        tracksListAdapter.setOnTrackClickedListener(this)
        mRecyclerViewTracks.adapter = this.tracksListAdapter
    }

    override fun bindTracks(tracks: List<Track>) {
        this.tracksListAdapter.bindTracks(tracks)
    }

    override fun refreshList() {
        tracksListAdapter.notifyDataSetChanged()
    }

    override fun onTrackClicked(track: Track) {
        getListeners().forEach {
            it.onTrackClicked(track)
        }
    }
}