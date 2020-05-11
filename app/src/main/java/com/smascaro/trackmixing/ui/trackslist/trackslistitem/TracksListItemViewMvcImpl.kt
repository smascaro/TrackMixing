package com.smascaro.trackmixing.ui.trackslist.trackslistitem

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.TextView
import com.smascaro.trackmixing.R
import com.smascaro.trackmixing.tracks.Track
import com.smascaro.trackmixing.ui.common.BaseObservableViewMvc

class TracksListItemViewMvcImpl(inflater: LayoutInflater, parent: ViewGroup?) :
    BaseObservableViewMvc<TracksListItemViewMvc.Listener>(), TracksListItemViewMvc {
    private lateinit var mTrack: Track
    private lateinit var mTrackTitleTxt: TextView
    private lateinit var mTrackIdTxt: TextView

    init {
        setRootView(inflater.inflate(R.layout.item_track, parent, false))
        mTrackTitleTxt = findViewById(R.id.trackTitle)
        mTrackIdTxt = findViewById(R.id.trackId)
        getRootView().setOnClickListener {
            getListeners().forEach { listener ->
                listener.onTrackClicked(mTrack)
            }
        }
    }

    override fun bindTrack(track: Track) {
        mTrack = track
        mTrackTitleTxt.text = track.title
        mTrackIdTxt.text = track.videoKey
    }

}