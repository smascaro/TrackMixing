package com.smascaro.trackmixing.trackslist.view

import android.animation.ArgbEvaluator
import android.animation.ValueAnimator
import android.content.SharedPreferences
import android.graphics.Color
import android.graphics.drawable.GradientDrawable
import android.os.Build
import android.view.View
import android.view.animation.AccelerateDecelerateInterpolator
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.SimpleItemAnimator
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.smascaro.trackmixing.R
import com.smascaro.trackmixing.common.data.model.Track
import com.smascaro.trackmixing.common.utils.ResourcesWrapper
import com.smascaro.trackmixing.common.utils.SHARED_PREFERENCES_PLAYBACK_IS_PLAYING
import com.smascaro.trackmixing.common.utils.SHARED_PREFERENCES_PLAYBACK_SONG_PLAYING
import com.smascaro.trackmixing.common.utils.SharedPreferencesFactory
import com.smascaro.trackmixing.common.view.architecture.BaseObservableViewMvc
import javax.inject.Inject

class TracksListViewMvcImpl @Inject constructor(
    private val tracksListAdapter: TracksListAdapter,
    resourcesWrapper: ResourcesWrapper
) : BaseObservableViewMvc<TracksListViewMvc.Listener>(),
    TracksListAdapter.Listener,
    TracksListViewMvc,
    SharedPreferences.OnSharedPreferenceChangeListener {

    private lateinit var mRecyclerViewTracks: RecyclerView
    private lateinit var backgroundGradient: View

    private val gradientCenterColor =
        resourcesWrapper.getColor(R.color.track_player_background_gradient_center_color)
    private val gradientEndColor =
        resourcesWrapper.getColor(R.color.track_player_background_gradient_end_color)
    private val defaultGradientStartColor = resourcesWrapper.getColor(R.color.colorAccent)

    private lateinit var sharedPreferences: SharedPreferences

    override fun bindRootView(rootView: View?) {
        super.bindRootView(rootView)
        initialize()
    }

    private fun initialize() {
        mRecyclerViewTracks = findViewById(R.id.rvTracks)
        backgroundGradient = findViewById(R.id.v_background_gradient)

        initializeRecyclerView()

        val fab = findViewById<FloatingActionButton>(R.id.fabTempMode)
        fab.setOnClickListener {
            getListeners().forEach {
                it.onSearchNavigationButtonClicked()
            }
        }
        setupSharedPreferences()
    }

    private fun initializeRecyclerView() {
        val layoutManagerWrapper = object : LinearLayoutManager(getContext()) {
            override fun supportsPredictiveItemAnimations(): Boolean {
                return false
            }
        }
        mRecyclerViewTracks.layoutManager = layoutManagerWrapper
        (mRecyclerViewTracks.itemAnimator as SimpleItemAnimator).supportsChangeAnimations = false
        mRecyclerViewTracks.setHasFixedSize(true)
        tracksListAdapter.setOnTrackClickedListener(this)
        mRecyclerViewTracks.adapter = this.tracksListAdapter
    }

    override fun bindTracks(tracks: List<Track>) {
        this.tracksListAdapter.bindTracks(tracks)
    }

    override fun refreshList() {
        tracksListAdapter.notifyDataSetChanged()
    }

    override fun updateBackgroundColor(newBackgroundColor: Int) {
        animateBackgroundGradientTo(newBackgroundColor)
    }

    override fun updateBackgroundColorToDefault() {
        animateBackgroundGradientTo(defaultGradientStartColor)
    }

    private fun animateBackgroundGradientTo(newBackgroundColor: Int) {
        val backgroundDrawable = backgroundGradient.background as GradientDrawable
        val initialColor =
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                backgroundDrawable.colors?.first() ?: Color.BLACK
            } else {
                Color.BLACK
            }
        val valueAnimator =
            ValueAnimator.ofObject(ArgbEvaluator(), initialColor, newBackgroundColor)
        valueAnimator.duration = 700
        valueAnimator.interpolator = AccelerateDecelerateInterpolator()
        val colorsArray =
            listOf(initialColor, gradientCenterColor, gradientEndColor).toIntArray()
        valueAnimator.addUpdateListener {
            colorsArray[0] = it.animatedValue as Int
            backgroundDrawable.colors = colorsArray
        }
        valueAnimator.start()
    }

    override fun onTrackClicked(track: Track) {
        getListeners().forEach {
            it.onTrackClicked(track)
        }
    }

    private fun setupSharedPreferences() {
        sharedPreferences =
            SharedPreferencesFactory.getPlaybackSharedPreferencesFactory(getContext()!!)
        sharedPreferences.registerOnSharedPreferenceChangeListener(this)
    }

    override fun onSharedPreferenceChanged(sharedPreferences: SharedPreferences?, key: String?) {
        if (key != null && key == SHARED_PREFERENCES_PLAYBACK_SONG_PLAYING || key == SHARED_PREFERENCES_PLAYBACK_IS_PLAYING) {
            getListeners().forEach {
                it.onPlayerStateChanged()
            }
        }
    }
}