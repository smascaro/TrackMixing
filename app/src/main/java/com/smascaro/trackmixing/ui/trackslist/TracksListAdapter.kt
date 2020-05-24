package com.smascaro.trackmixing.ui.trackslist

import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.card.MaterialCardView
import com.smascaro.trackmixing.tracks.Track
import com.smascaro.trackmixing.ui.common.ViewMvcFactory
import com.smascaro.trackmixing.ui.trackslist.trackslistitem.TracksListItemViewMvc
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class TracksListAdapter(
    private val mListener: Listener,
    private val mViewMvcFactory: ViewMvcFactory
) : RecyclerView.Adapter<TracksListAdapter.ViewHolder>(), TracksListItemViewMvc.Listener {
    interface Listener {
        fun onTrackClicked(
            track: Track,
            card: MaterialCardView
        )
    }

    class ViewHolder(val mViewMvc: TracksListItemViewMvc) :
        RecyclerView.ViewHolder(mViewMvc.getRootView())

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val viewMvc = mViewMvcFactory.getTracksListItemViewMvc(parent)
        viewMvc.registerListener(this)
        return ViewHolder(viewMvc)
    }

    private var mTracks = mutableListOf<Track>()

    fun bindTracks(tracks: List<Track>) {
        mTracks = tracks.toMutableList()
        CoroutineScope(Dispatchers.Main).launch {
            notifyDataSetChanged()
        }
    }


    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.mViewMvc.bindTrack(mTracks[position])
    }

    override fun onTrackClicked(track: Track, card: MaterialCardView) {
        mListener.onTrackClicked(track, card)
    }

    override fun getItemCount(): Int {
        return mTracks.size
    }

}
