package com.smascaro.trackmixing.search.view.resultitem

import android.widget.ImageView
import com.bumptech.glide.RequestManager
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.google.android.material.textview.MaterialTextView
import com.smascaro.trackmixing.R
import com.smascaro.trackmixing.base.time.TimeHelper
import com.smascaro.trackmixing.common.utils.ResourcesWrapper
import com.smascaro.trackmixing.common.view.architecture.BaseObservableViewMvc
import com.smascaro.trackmixing.search.model.SearchResult

class SearchResultsItemViewMvcImpl(
    private val glide: RequestManager,
    private val resources: ResourcesWrapper
) :
    BaseObservableViewMvc<SearchResultsItemViewMvc.Listener>(),
    SearchResultsItemViewMvc {
    private lateinit var searchResult: SearchResult
    private var position: Int = -1
    private lateinit var searchResultThumbnailImageView: ImageView
    private lateinit var searchResultTitleTextView: MaterialTextView
    private lateinit var searchResultDetailsTextView: MaterialTextView

    override fun initialize() {
        super.initialize()
        searchResultThumbnailImageView = findViewById(R.id.iv_item_track_thumbnail)
        searchResultTitleTextView = findViewById(R.id.tv_item_track_title)
        searchResultDetailsTextView = findViewById(R.id.tv_item_track_details)
    }

    override fun initializeListeners() {
        super.initializeListeners()
        getRootView().setOnClickListener {
            getListeners().forEach {
                it.onSearchResultClicked(searchResult)
            }
        }

        getRootView().setOnLongClickListener {
            it.isSelected = true
            true
        }
    }

    override fun bindResult(result: SearchResult) {
        this.searchResult = result
        searchResultTitleTextView.text = this.searchResult.title
        val authorText = this.searchResult.author
        val durationText =
            TimeHelper.fromSeconds(this.searchResult.secondsLong.toLong()).toStringRepresentation()
        val statusText = "Tap to download"
        searchResultDetailsTextView.text =
            resources.getString(R.string.track_item_data_template, authorText, durationText, statusText)
        glide
            .asBitmap()
            .load(this.searchResult.thumbnailUrl)
            .diskCacheStrategy(DiskCacheStrategy.AUTOMATIC)
            .into(searchResultThumbnailImageView)
    }

    override fun bindPosition(position: Int) {
        this.position = position
    }
}