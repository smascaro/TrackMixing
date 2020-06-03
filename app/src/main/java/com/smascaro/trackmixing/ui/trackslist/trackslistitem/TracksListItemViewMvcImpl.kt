package com.smascaro.trackmixing.ui.trackslist.trackslistitem

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.google.android.material.card.MaterialCardView
import com.smascaro.trackmixing.R
import com.smascaro.trackmixing.tracks.Track
import com.smascaro.trackmixing.ui.common.BaseObservableViewMvc

class TracksListItemViewMvcImpl(inflater: LayoutInflater, parent: ViewGroup?) :
    BaseObservableViewMvc<TracksListItemViewMvc.Listener>(), TracksListItemViewMvc {
    private lateinit var mTrack: Track
    private lateinit var mCard: MaterialCardView
    private lateinit var mTrackTitleTxt: TextView
    private lateinit var mTrackThumbnailImg: ImageView

    init {
        setRootView(inflater.inflate(R.layout.item_track, parent, false))
        mCard = getRootView() as MaterialCardView
        mTrackTitleTxt = findViewById(R.id.trackTitle)
        mTrackThumbnailImg = findViewById(R.id.thumbnailImg)
        getRootView().setOnClickListener {
            getListeners().forEach { listener ->
                listener.onTrackClicked(mTrack, mCard)
            }
        }
    }

    override fun bindTrack(track: Track) {
        mTrack = track
        mTrackTitleTxt.text = track.title
        Glide
            .with(getRootView().context)
            .load(mTrack.thumbnailUrl)
            .diskCacheStrategy(DiskCacheStrategy.AUTOMATIC)
            .into(mTrackThumbnailImg)
        mCard.transitionName = track.videoKey
        mTrackTitleTxt.transitionName = track.title
    }

}