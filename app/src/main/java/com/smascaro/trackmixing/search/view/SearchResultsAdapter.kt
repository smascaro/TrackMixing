package com.smascaro.trackmixing.search.view

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.smascaro.trackmixing.R
import com.smascaro.trackmixing.base.time.TimeHelper
import com.smascaro.trackmixing.databinding.ItemTrackBinding
import com.smascaro.trackmixing.search.model.SearchResult
import javax.inject.Inject

class SearchResultsAdapter @Inject constructor() : RecyclerView.Adapter<SearchResultsAdapter.ViewHolder>() {
    fun interface Listener {
        fun onSearchResultClicked(searchResult: SearchResult)
    }

    private var listener: Listener? = null
    private var searchResults = mutableListOf<SearchResult>()

    fun setOnSearchResultClickedListener(listener: Listener) {
        this.listener = listener
    }

    fun removeOnSearchResultClickedListener() {
        this.listener = null
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemTrackBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    fun bindResults(results: List<SearchResult>) {
        searchResults = results.toMutableList()
        notifyDataSetChanged()
    }

    override fun getItemCount(): Int {
        return searchResults.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bindResult(searchResults[position])
    }

    inner class ViewHolder(private val binding: ItemTrackBinding) :
        RecyclerView.ViewHolder(binding.root) {
        private lateinit var searchResult: SearchResult

        init {
            binding.root.setOnClickListener {
                listener?.onSearchResultClicked(searchResult)
            }
        }

        fun bindResult(result: SearchResult) {
            this.searchResult = result
            binding.tvItemTrackTitle.text = this.searchResult.title
            val authorText = this.searchResult.author
            val durationText =
                TimeHelper.fromSeconds(this.searchResult.secondsLong.toLong()).toStringRepresentation()
            val statusText = "Tap to download"
            binding.tvItemTrackDetails.text =
                binding.root.resources.getString(
                    R.string.track_item_data_template,
                    authorText,
                    durationText,
                    statusText
                )
            Glide.with(binding.root)
                .asBitmap()
                .load(this.searchResult.thumbnailUrl)
                .diskCacheStrategy(DiskCacheStrategy.AUTOMATIC)
                .into(binding.ivItemTrackThumbnail)
        }
    }
}