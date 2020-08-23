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
import com.smascaro.trackmixing.common.utils.ResourcesWrapper
import com.smascaro.trackmixing.common.view.architecture.BaseObservableViewMvc
import com.smascaro.trackmixing.playbackservice.utils.PlaybackSession
import javax.inject.Inject

class TracksListItemViewMvcImpl @Inject constructor(
    val glide: RequestManager,
    private val playbackSession: PlaybackSession,
    resources: ResourcesWrapper
) :
    BaseObservableViewMvc<TracksListItemViewMvc.Listener>(),
    TracksListItemViewMvc {
    private lateinit var mTrack: Track
    private lateinit var mCard: MaterialCardView
    private lateinit var mTrackTitleTxt: TextView
    private lateinit var mTrackThumbnailImg: ImageView
    private lateinit var mExpandArrowImg: ImageView

    private lateinit var mExpandView: LinearLayout
    private lateinit var mExpandTitle: MaterialTextView

    private var mPosition: Int = -1

    private var mIsExpanded: Boolean = false

    private val expandArrowInitialRotationDegrees =
        resources.getInteger(R.integer.item_track_expand_arrow_initial_rotation).toFloat()
    private val expandArrowBackRotationDegrees =
        resources.getInteger(R.integer.item_track_expand_arrow_back_rotation).toFloat()
    private val expandArrowAnimationDuration =
        resources.getLong(R.integer.item_track_expand_arrow_animation_duration)

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
        mExpandArrowImg = findViewById(R.id.expandArrow)
        mExpandArrowImg.setOnClickListener {
            mIsExpanded = !mIsExpanded
            rotateExpandArrow()
            getListeners().forEach { listener ->
                listener.onExpandOrCollapseDetailsRequest(mPosition)
            }
        }
        getRootView().setOnClickListener {
            playbackSession.startPlayback(mTrack)
        }
        mExpandView.setOnClickListener {
            getListeners().forEach {
                it.onTrackClicked(mTrack, mCard)
            }
        }
    }

    private fun rotateExpandArrow() {
        mExpandArrowImg.animate()
            .rotationBy(if (mIsExpanded) expandArrowBackRotationDegrees else expandArrowInitialRotationDegrees)
            .apply {
                duration = expandArrowAnimationDuration
            }.start()
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