package com.smascaro.trackmixing.trackslist.view.listitem

import android.widget.ImageView
import android.widget.TextView
import com.bumptech.glide.RequestManager
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.smascaro.trackmixing.R
import com.smascaro.trackmixing.base.model.Track
import com.smascaro.trackmixing.base.time.TimeHelper
import com.smascaro.trackmixing.common.utils.ResourcesWrapper
import com.smascaro.trackmixing.common.view.architecture.BaseObservableViewMvc
import javax.inject.Inject

class TracksListItemViewMvcImpl @Inject constructor(
    val glide: RequestManager,
    val resources: ResourcesWrapper
) :
    BaseObservableViewMvc<TracksListItemViewMvc.Listener>(),
    TracksListItemViewMvc {
    private lateinit var track: Track
    private lateinit var trackTitleTxt: TextView
    private lateinit var trackDetailsTxt: TextView
    private lateinit var trackThumbnailImg: ImageView
    private var position: Int = -1

    override fun initialize() {
        super.initialize()
        trackTitleTxt = findViewById(R.id.tv_item_track_title)
        trackThumbnailImg = findViewById(R.id.iv_item_track_thumbnail)
        trackDetailsTxt = findViewById(R.id.tv_item_track_details)
    }

    override fun initializeListeners() {
        super.initializeListeners()
        getRootView().setOnClickListener {
            getListeners().forEach {
                it.onTrackClicked(track)
            }
        }

        getRootView().setOnLongClickListener {
            it.isSelected = true
            true
        }
    }

    override fun bindTrack(track: Track) {
        this.track = track
        trackTitleTxt.text = track.title
        val authorText = track.author
        val durationText = TimeHelper.fromSeconds(track.secondsLong.value).toStringRepresentation()
        val stateText = TimeHelper.elapsedTime(track.requestedTimestamp)
        trackDetailsTxt.text =
            resources.getString(R.string.track_item_data_template, authorText, durationText, stateText)
        glide
            .asBitmap()
            .load(this.track.thumbnailUrl)
            .diskCacheStrategy(DiskCacheStrategy.AUTOMATIC)
            .into(trackThumbnailImg)
        trackTitleTxt.transitionName = track.title
    }

    override fun bindPosition(position: Int) {
        this.position = position
    }
}