package com.smascaro.trackmixing.trackslist.view

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.smascaro.trackmixing.R
import com.smascaro.trackmixing.base.model.Track
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
        fun onTrackClicked(track: Track)
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

    fun List<Int>.isConsecutive(): Boolean {
        if (this.size == 0) {
            return false
        } else {
            val previous = this[0]
            for (i in 1 until this.size) {
                val current = this[i]
                if (current != previous + 1) {
                    return false
                }
            }
            return true
        }
    }

    fun bindTracks(tracks: List<Track>) {
        val insertedIndexes = findInsertedItems(tracks).sorted()
        mTracks = tracks.toMutableList()
        CoroutineScope(Dispatchers.Main).launch {
            if (insertedIndexes.size == 1) {
                notifyItemInserted(insertedIndexes.first())
            } else if (insertedIndexes.isConsecutive()) {
                notifyItemRangeInserted(insertedIndexes.first(), insertedIndexes.size)
            } else {
                notifyDataSetChanged()
            }
        }
    }

    private fun findInsertedItems(newTracks: List<Track>): List<Int> {
        val newIndexes = mutableListOf<Int>()
        newTracks.forEachIndexed { index, track ->
            if (!mTracks.any { it.videoKey == track.videoKey }) {
                newIndexes.add(index)
            }
        }
        return newIndexes
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.mViewMvc.bindTrack(mTracks[position])
        holder.mViewMvc.bindPosition(holder.adapterPosition)
    }

    override fun onTrackClicked(track: Track) {
        listener?.onTrackClicked(track)
    }

    override fun onExpandOrCollapseDetailsRequest(
        itemPosition: Int
    ) {
        notifyItemChanged(itemPosition)
    }

    override fun getItemCount(): Int {
        return mTracks.size
    }
}
