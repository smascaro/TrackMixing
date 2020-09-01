package com.smascaro.trackmixing.search.view.resultitem

import android.view.View
import android.widget.ImageView
import com.bumptech.glide.RequestManager
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.google.android.material.textview.MaterialTextView
import com.smascaro.trackmixing.R
import com.smascaro.trackmixing.common.utils.TimeHelper
import com.smascaro.trackmixing.common.view.architecture.BaseObservableViewMvc
import com.smascaro.trackmixing.search.model.SearchResult

class SearchResultsItemViewMvcImpl(private val glide: RequestManager) :
    BaseObservableViewMvc<SearchResultsItemViewMvc.Listener>(),
    SearchResultsItemViewMvc {
    private lateinit var searchResult: SearchResult
    private var position: Int = -1

    private lateinit var searchResultThumbnailImageView: ImageView
    private lateinit var searchResultTitleTextView: MaterialTextView
    private lateinit var searchResultAuthorTextView: MaterialTextView
    private lateinit var searchResultDurationTextView: MaterialTextView
    private lateinit var searchResultStatusTextView: MaterialTextView

    override fun bindRootView(rootView: View?) {
        super.bindRootView(rootView)
        initialize()
    }

    private fun initialize() {
        searchResultThumbnailImageView = findViewById(R.id.iv_item_track_thumbnail)
        searchResultTitleTextView = findViewById(R.id.tv_item_track_title)
        searchResultAuthorTextView = findViewById(R.id.tv_item_track_author)
        searchResultDurationTextView = findViewById(R.id.tv_item_track_duration)
        searchResultStatusTextView = findViewById(R.id.tv_item_track_status)

        getRootView().setOnClickListener {
            getListeners().forEach {
                it.onSearchResultClicked(searchResult)
            }
        }
    }

    override fun bindResult(result: SearchResult) {
        this.searchResult = result
        searchResultTitleTextView.text = this.searchResult.title
        searchResultAuthorTextView.text = this.searchResult.author
        searchResultDurationTextView.text =
            TimeHelper.fromSeconds(this.searchResult.secondsLong.toLong()).toStringRepresentation()
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