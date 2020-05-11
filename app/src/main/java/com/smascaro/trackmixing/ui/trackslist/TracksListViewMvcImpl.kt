package com.smascaro.trackmixing.ui.trackslist

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.smascaro.trackmixing.R
import com.smascaro.trackmixing.tracks.Track
import com.smascaro.trackmixing.ui.common.BaseObservableViewMvc
import com.smascaro.trackmixing.ui.common.ViewMvcFactory
import java.util.*

class TracksListViewMvcImpl(
    inflater: LayoutInflater,
    val parent: ViewGroup?,
    viewMvcFactory: ViewMvcFactory
) : BaseObservableViewMvc<TracksListViewMvc.Listener>(),
    TracksListAdapter.Listener,
    TracksListViewMvc {

    private val mRecyclerViewTracks: RecyclerView
    private val mRecyclerViewTracksAdapter: TracksListAdapter

    init {
        setRootView(inflater.inflate(R.layout.activity_tracks_list, parent, false))
        mRecyclerViewTracks = findViewById(R.id.rvTracks)
        mRecyclerViewTracks.layoutManager = LinearLayoutManager(getContext())
        mRecyclerViewTracksAdapter = TracksListAdapter(this, viewMvcFactory)
        mRecyclerViewTracks.adapter = mRecyclerViewTracksAdapter
    }

    override fun bindTracks(tracks: List<Track>) {
        mRecyclerViewTracksAdapter.bindTracks(tracks)
    }


    override fun onTrackClicked(track: Track) {
        getListeners().forEach { listener ->
            listener.onTrackClicked(track)
        }
    }

}