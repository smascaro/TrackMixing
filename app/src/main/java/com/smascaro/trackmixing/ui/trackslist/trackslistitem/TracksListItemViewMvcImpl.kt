package com.smascaro.trackmixing.ui.trackslist.trackslistitem

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import com.bumptech.glide.Glide
import com.bumptech.glide.RequestManager
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.google.android.material.card.MaterialCardView
import com.google.android.material.textview.MaterialTextView
import com.smascaro.trackmixing.R
import com.smascaro.trackmixing.common.di.DaggerAppComponent
import com.smascaro.trackmixing.tracks.Track
import com.smascaro.trackmixing.ui.common.BaseObservableViewMvc
import javax.inject.Inject

class TracksListItemViewMvcImpl(inflater: LayoutInflater, parent: ViewGroup?) :
    BaseObservableViewMvc<TracksListItemViewMvc.Listener>(), TracksListItemViewMvc {
    private lateinit var mTrack: Track
    private lateinit var mCard: MaterialCardView
    private lateinit var mTrackTitleTxt: TextView
    private lateinit var mTrackThumbnailImg: ImageView

    private lateinit var mExpandView: LinearLayout
    private lateinit var mExpandTitle: MaterialTextView

    private var mPosition: Int = -1

    lateinit var glideRequestManager: RequestManager

    //    private lateinit var mParentLayout: LinearLayout
    private var mIsExpanded: Boolean = false

    init {
        setRootView(inflater.inflate(R.layout.item_track, parent, false))
        glideRequestManager = Glide.with(getContext()!!)
        mCard = findViewById(R.id.cardViewContainer)
        mTrackTitleTxt = findViewById(R.id.trackTitle)
        mTrackThumbnailImg = findViewById(R.id.thumbnailImg)
        mExpandView = findViewById(R.id.expandedView)
        mExpandTitle = findViewById(R.id.expandedTitle)

//        mParentLayout=findViewById(R.id.parentItemLayout)
        getRootView().setOnClickListener {
            mIsExpanded = !mIsExpanded
            getListeners().forEach { listener ->
//                listener.onTrackClicked(mTrack, mCard)
//                listener.onExpandOrCollapseDetailsRequest(mTrack, mExpandView, mIsExpanded)
                listener.onExpandOrCollapseDetailsRequest(mPosition)
            }
        }
        mExpandView.setOnClickListener {
            getListeners().forEach {
                it.onTrackClicked(mTrack, mCard)
            }
        }
    }

    override fun bindTrack(track: Track) {
        mTrack = track
        mTrackTitleTxt.text = track.title
        mExpandTitle.text = track.title
        mExpandView.visibility = if (mIsExpanded) {
            View.VISIBLE
        } else {
            View.GONE
        }
        glideRequestManager
            .asBitmap()
            .load(mTrack.thumbnailUrl)
            .diskCacheStrategy(DiskCacheStrategy.AUTOMATIC)
            .into(mTrackThumbnailImg)
        mCard.transitionName = track.videoKey
        mTrackTitleTxt.transitionName = track.title
    }

    override fun bindPosition(position: Int) {
        mPosition = position
    }

}