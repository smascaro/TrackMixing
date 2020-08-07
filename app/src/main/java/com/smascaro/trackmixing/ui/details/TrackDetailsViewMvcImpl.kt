package com.smascaro.trackmixing.ui.details

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ImageView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.google.android.material.button.MaterialButton
import com.google.android.material.textview.MaterialTextView
import com.smascaro.trackmixing.R
import com.smascaro.trackmixing.tracks.Track
import com.smascaro.trackmixing.ui.common.BaseObservableViewMvc

class TrackDetailsViewMvcImpl(
    inflater: LayoutInflater,
    val parent: ViewGroup?,
    private val mTrack: Track
) : BaseObservableViewMvc<TrackDetailsViewMvc.Listener>(), TrackDetailsViewMvc {

    private lateinit var btnGoToPlayer: MaterialButton
    private lateinit var mTrackTitleTxt: MaterialTextView
    private var mImageThumbnail: ImageView

    init {
        bindRootView(inflater.inflate(R.layout.fragment_track_details, parent, false))
//        val root: ConstraintLayout = findViewById(R.id.container_details)
//        getRootView().transitionName=mTrack.videoKey
//        root.transitionName=mTrack.videoKey
        mImageThumbnail = findViewById(R.id.thumbnail_detail)
        mTrackTitleTxt = findViewById(R.id.track_title_detail)
        getRootView().transitionName = mTrack.videoKey
        mTrackTitleTxt.transitionName = mTrack.title
        Glide
            .with(getRootView().context)
            .load(mTrack.thumbnailUrl)
            .diskCacheStrategy(DiskCacheStrategy.AUTOMATIC)
            .into(mImageThumbnail)
        mTrackTitleTxt.text = mTrack.title
    }

    override fun initUI() {
        btnGoToPlayer = findViewById(R.id.go_to_player_btn_details)
        btnGoToPlayer.setOnClickListener {
            getListeners().forEach {
                it.onGoToPlayerButtonClicked(mTrack)
            }
        }
    }


}