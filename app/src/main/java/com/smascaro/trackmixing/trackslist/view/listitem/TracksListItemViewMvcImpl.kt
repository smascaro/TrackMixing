package com.smascaro.trackmixing.trackslist.view.listitem

import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import com.bumptech.glide.RequestManager
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.google.android.material.card.MaterialCardView
import com.google.android.material.textview.MaterialTextView
import com.smascaro.trackmixing.R
import com.smascaro.trackmixing.common.data.model.Track
import com.smascaro.trackmixing.common.view.architecture.BaseObservableViewMvc
import javax.inject.Inject

class TracksListItemViewMvcImpl @Inject constructor(val glide: RequestManager) :
    BaseObservableViewMvc<TracksListItemViewMvc.Listener>(),
    TracksListItemViewMvc {
    private lateinit var mTrack: Track
    private lateinit var mCard: MaterialCardView
    private lateinit var mTrackTitleTxt: TextView
    private lateinit var mTrackThumbnailImg: ImageView

    private lateinit var mExpandView: LinearLayout
    private lateinit var mExpandTitle: MaterialTextView

    private var mPosition: Int = -1

    private var mIsExpanded: Boolean = false


    override fun bindRootView(rootView: View?) {
        super.bindRootView(rootView)
        initialize()
    }

    private fun initialize() {
        mCard = findViewById(R.id.cardViewContainer)
        mTrackTitleTxt = findViewById(R.id.trackTitle)
        mTrackThumbnailImg = findViewById(R.id.thumbnailImg)
        mExpandView = findViewById(R.id.expandedView)
        mExpandTitle = findViewById(R.id.expandedTitle)

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
        glide
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