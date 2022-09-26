package com.smascaro.trackmixing.search.view

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.smascaro.trackmixing.R
import com.smascaro.trackmixing.base.time.TimeHelper
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
        val rootView = LayoutInflater.from(parent.context).inflate(R.layout.item_track, parent, false)
        return ViewHolder(rootView)
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

    inner class ViewHolder(private val rootView: View) :
        RecyclerView.ViewHolder(rootView) {
        private lateinit var searchResult: SearchResult
        private var searchResultThumbnailImageView: ImageView =
            rootView.findViewById(R.id.iv_item_track_thumbnail)
        private var searchResultTitleTextView: TextView = rootView.findViewById(R.id.tv_item_track_title)
        private var searchResultDetailsTextView: TextView = rootView.findViewById(R.id.tv_item_track_details)

        init {
            rootView.setOnClickListener {
                listener?.onSearchResultClicked(searchResult)
            }
        }

        fun bindResult(result: SearchResult) {
            this.searchResult = result
            searchResultTitleTextView.text = this.searchResult.title
            val authorText = this.searchResult.author
            val durationText =
                TimeHelper.fromSeconds(this.searchResult.secondsLong.toLong()).toStringRepresentation()
            val statusText = "Tap to download"
            searchResultDetailsTextView.text =
                rootView.resources.getString(
                    R.string.track_item_data_template,
                    authorText,
                    durationText,
                    statusText
                )
            Glide.with(rootView)
                .asBitmap()
                .load(this.searchResult.thumbnailUrl)
                .diskCacheStrategy(DiskCacheStrategy.AUTOMATIC)
                .into(searchResultThumbnailImageView)
        }
    }
}