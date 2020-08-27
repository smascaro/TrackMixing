package com.smascaro.trackmixing.trackslist.view

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.card.MaterialCardView
import com.smascaro.trackmixing.R
import com.smascaro.trackmixing.common.data.model.Track
import com.smascaro.trackmixing.common.utils.DaggerViewMvcFactory
import com.smascaro.trackmixing.trackslist.view.listitem.TracksListItemViewMvc
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

class TracksListAdapter @Inject constructor(
    private val viewMvcFactory: DaggerViewMvcFactory
) : RecyclerView.Adapter<TracksListAdapter.ViewHolder>(), TracksListItemViewMvc.Listener {
    interface Listener {
        fun onTrackClicked(
            track: Track,
            card: MaterialCardView
        )
    }

    class ViewHolder(val mViewMvc: TracksListItemViewMvc) :
        RecyclerView.ViewHolder(mViewMvc.getRootView())

    private var listener: Listener? = null


    fun setOnTrackClickedListener(listener: Listener) {
        this.listener = listener
    }

    fun removeOnTrackClickedListener() {
        this.listener = null
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val viewMvc = viewMvcFactory.getTracksListItemViewMvc()
        viewMvc.bindRootView(
            LayoutInflater.from(parent.context).inflate(R.layout.item_track, parent, false)
        )
        viewMvc.registerListener(this)
        return ViewHolder(
            viewMvc
        )
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
        holder.mViewMvc.bindPosition(holder.adapterPosition)
    }

    override fun onTrackClicked(track: Track, card: MaterialCardView) {
        listener?.onTrackClicked(track, card)
    }

    override fun onExpandOrCollapseDetailsRequest(
        itemPosition: Int
    ) {
//        if(expandRequest) {
//            Animations.expandDetails(layoutToExpand)
//        } else {
//            Animations.collapseDetails(layoutToExpand)
//        }
        notifyItemChanged(itemPosition)
    }

    override fun getItemCount(): Int {
        return mTracks.size
    }

}
