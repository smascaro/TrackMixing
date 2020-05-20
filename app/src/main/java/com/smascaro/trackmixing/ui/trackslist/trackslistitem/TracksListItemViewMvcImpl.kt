package com.smascaro.trackmixing.ui.trackslist.trackslistitem

import android.content.res.Resources
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.TextView
import androidx.constraintlayout.motion.widget.MotionLayout
import androidx.constraintlayout.widget.ConstraintLayout
import coil.api.load
import coil.transition.CrossfadeTransition
import com.smascaro.trackmixing.R
import com.smascaro.trackmixing.common.elapsed
import com.smascaro.trackmixing.common.toMinutesAndSecondsRepresentation
import com.smascaro.trackmixing.tracks.Track
import com.smascaro.trackmixing.ui.common.BaseObservableViewMvc

class TracksListItemViewMvcImpl(inflater: LayoutInflater, parent: ViewGroup?) :
    BaseObservableViewMvc<TracksListItemViewMvc.Listener>(), TracksListItemViewMvc {
    private lateinit var mTrack: Track
    private lateinit var mTrackTitleTxt: TextView
    private lateinit var mTrackThumbnailImg: ImageView
    init {
        setRootView(inflater.inflate(R.layout.item_track, parent, false))
        mTrackTitleTxt = findViewById(R.id.trackTitle)
        mTrackThumbnailImg = findViewById(R.id.thumbnailImg)
        getRootView().setOnClickListener {
            getListeners().forEach { listener ->
                listener.onTrackClicked(mTrack)
            }
        }
    }

    override fun bindTrack(track: Track) {
        mTrack = track
        mTrackTitleTxt.text = track.title
        mTrackThumbnailImg.load(track.thumbnailUrl) {
            placeholder(R.drawable.ic_placeholder_64)
            crossfade(true)
            transition(CrossfadeTransition())
        }
    }

}