package com.smascaro.trackmixing.ui.trackslist

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ImageView
import androidx.constraintlayout.motion.widget.MotionLayout
import androidx.navigation.fragment.FragmentNavigatorExtras
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.SimpleItemAnimator
import com.google.android.material.card.MaterialCardView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.textview.MaterialTextView
import com.smascaro.trackmixing.R
import com.smascaro.trackmixing.tracks.Track
import com.smascaro.trackmixing.ui.common.BaseObservableViewMvc
import com.smascaro.trackmixing.ui.common.ViewMvcFactory
import com.smascaro.trackmixing.ui.common.navigationhelper.NavigationHelper

class TracksListViewMvcImpl(
    inflater: LayoutInflater,
    val parent: ViewGroup?,
    viewMvcFactory: ViewMvcFactory
) : BaseObservableViewMvc<TracksListViewMvc.Listener>(),
    TracksListAdapter.Listener,
    TracksListViewMvc {

    private val mRecyclerViewTracks: RecyclerView
    private val mRecyclerViewTracksAdapter: TracksListAdapter
    private lateinit var mNavigationHelper: NavigationHelper
    private lateinit var mMotionLayout: MotionLayout
    private var currentDataSource: TracksListViewMvc.TracksDataSource =
        TracksListViewMvc.TracksDataSource.DATABASE

    init {
        setRootView(inflater.inflate(R.layout.fragment_tracks_list, parent, false))
        mRecyclerViewTracks = findViewById(R.id.rvTracks)
        mRecyclerViewTracks.layoutManager = LinearLayoutManager(getContext())
        (mRecyclerViewTracks.itemAnimator as SimpleItemAnimator).supportsChangeAnimations = false
        mRecyclerViewTracks.setHasFixedSize(true)
        mRecyclerViewTracksAdapter = TracksListAdapter(this, viewMvcFactory)
        mRecyclerViewTracks.adapter = mRecyclerViewTracksAdapter
        mMotionLayout = findViewById(R.id.motionLayoutFloatingCard)
        val fab = findViewById<FloatingActionButton>(R.id.fabTempMode)
        fab.setOnClickListener {
            currentDataSource = when (currentDataSource) {
                TracksListViewMvc.TracksDataSource.DATABASE -> TracksListViewMvc.TracksDataSource.SERVER
                TracksListViewMvc.TracksDataSource.SERVER -> TracksListViewMvc.TracksDataSource.DATABASE
            }
            getListeners().forEach {
                it.onCurrentDataSourceRequest(currentDataSource)
            }
        }

    }

    override fun bindTracks(tracks: List<Track>) {
        mRecyclerViewTracksAdapter.bindTracks(tracks)
    }

    override fun bindNavigationHelper(navigationHelper: NavigationHelper) {
        mNavigationHelper = navigationHelper
    }

    override fun getCurrentDataSource(): TracksListViewMvc.TracksDataSource {
        return currentDataSource
    }

    override fun navigateToPlayer(track: Track) {
        mNavigationHelper.toPlayer(track)
    }

    override fun displayFloatingCard() {
        if (mMotionLayout.progress == 0.0f) {
            mMotionLayout.transitionToEnd()
        }
    }

    override fun showDetails(track: Track) {

    }


    override fun onTrackClicked(
        track: Track,
        card: MaterialCardView
    ) {
//        getListeners().forEach { listener ->
//            listener.onTrackClicked(track)
//        }
        val title = card.findViewById<MaterialTextView>(R.id.trackTitle)
        val imageView = card.findViewById<ImageView>(R.id.thumbnailImg)
        val extras = FragmentNavigatorExtras(
            card to track.videoKey,
            title to track.title
        )
        mNavigationHelper.toDetails(track, extras)
    }


}