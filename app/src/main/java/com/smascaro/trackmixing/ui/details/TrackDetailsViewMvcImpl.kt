package com.smascaro.trackmixing.ui.details

import android.view.View
import android.widget.ImageView
import com.bumptech.glide.RequestManager
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.google.android.material.button.MaterialButton
import com.google.android.material.textview.MaterialTextView
import com.smascaro.trackmixing.R
import com.smascaro.trackmixing.errorhandling.ViewMvcNotInitialized
import com.smascaro.trackmixing.tracks.Track
import com.smascaro.trackmixing.ui.common.BaseObservableViewMvc
import javax.inject.Inject

class TrackDetailsViewMvcImpl @Inject constructor(
    private val glide: RequestManager
) : BaseObservableViewMvc<TrackDetailsViewMvc.Listener>(), TrackDetailsViewMvc {

    private lateinit var btnGoToPlayer: MaterialButton
    private lateinit var mTrackTitleTxt: MaterialTextView
    private lateinit var mImageThumbnail: ImageView

    private lateinit var track: Track
    override fun bindRootView(rootView: View?) {
        super.bindRootView(rootView)
        initializeIfReady()
    }

    override fun bindTrack(track: Track) {
        this.track = track
        initializeIfReady()
    }

    private fun initialize() {
        mImageThumbnail = findViewById(R.id.thumbnail_detail)
        mTrackTitleTxt = findViewById(R.id.track_title_detail)
        getRootView().transitionName = track.videoKey
        mTrackTitleTxt.transitionName = track.title
        glide
            .load(track.thumbnailUrl)
            .diskCacheStrategy(DiskCacheStrategy.AUTOMATIC)
            .into(mImageThumbnail)
        mTrackTitleTxt.text = track.title
    }


    override fun initUI() {
        if (!isInitialized()) {
            throw ViewMvcNotInitialized("Track and root view must be bound first")
        }
        btnGoToPlayer = findViewById(R.id.go_to_player_btn_details)
        btnGoToPlayer.setOnClickListener {
            getListeners().forEach {
                it.onGoToPlayerButtonClicked(track)
            }
        }
    }

    private fun initializeIfReady() {
        if (isInitialized()) {
            initialize()
        }
    }

    private fun isInitialized(): Boolean {
        var isInitialized: Boolean = true
        try {
            if (this::track.isInitialized) {
                getRootView().context
            } else {
                throw Exception("Track must be bound ")
            }
        } catch (e: Exception) {
            isInitialized = false
        }
        return isInitialized
    }
}