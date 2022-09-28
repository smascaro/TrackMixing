package com.smascaro.trackmixing.trackslist.view

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.smascaro.trackmixing.R
import com.smascaro.trackmixing.base.data.model.Track
import com.smascaro.trackmixing.base.time.TimeHelper
import com.smascaro.trackmixing.databinding.ItemTrackBinding

class TracksListAdapter : RecyclerView.Adapter<TracksListAdapter.ViewHolder>() {
    fun interface Listener {
        fun onTrackClicked(track: Track)
    }

    private var mTracks = mutableListOf<Track>()

    private var listener: Listener? = null

    fun setOnTrackClickedListener(listener: Listener) {
        this.listener = listener
    }

    fun removeOnTrackClickedListener() {
        this.listener = null
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemTrackBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    fun bindTracks(tracks: List<Track>) {
        val insertedIndexes = findInsertedItems(tracks).sorted()
        mTracks = tracks.toMutableList()
        if (insertedIndexes.any()) {
            insertedIndexes.forEach {
                notifyItemInserted(it)
            }
        } else notifyDataSetChanged()
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
        holder.bindTrack(mTracks[position])
    }

    override fun getItemCount(): Int {
        return mTracks.size
    }

    inner class ViewHolder(private val binding: ItemTrackBinding) :
        RecyclerView.ViewHolder(binding.root) {
        private var track: Track? = null

        init {
            binding.root.setOnClickListener {
                track?.let {
                    listener?.onTrackClicked(it)
                }
            }

            binding.root.setOnLongClickListener {
                it.isSelected = true
                true
            }
        }

        fun bindTrack(track: Track) {
            this.track = track
            binding.tvItemTrackTitle.text = track.title
            val authorText = track.author
            val durationText = TimeHelper.fromSeconds(track.secondsLong.value).toStringRepresentation()
            val stateText = TimeHelper.elapsedTime(track.requestedTimestamp)
            binding.tvItemTrackDetails.text =
                binding.root.context.getString(
                    R.string.track_item_data_template,
                    authorText,
                    durationText,
                    stateText
                )
            Glide.with(binding.root)
                .asBitmap()
                .load(track.thumbnailUrl)
                .diskCacheStrategy(DiskCacheStrategy.AUTOMATIC)
                .into(binding.ivItemTrackThumbnail)
            binding.tvItemTrackTitle.transitionName = track.title
        }
    }
}
