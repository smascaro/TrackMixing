package com.smascaro.trackmixing.ui.main

//import com.smascaro.trackmixing.common.di.PlaybackSharedPreferences
import android.content.SharedPreferences
import android.view.LayoutInflater
import android.view.View
import android.widget.ImageView
import androidx.constraintlayout.widget.ConstraintLayout
import com.bumptech.glide.RequestManager
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.request.target.BitmapImageViewTarget
import com.google.android.material.textview.MaterialTextView
import com.smascaro.trackmixing.R
import com.smascaro.trackmixing.common.SHARED_PREFERENCES_PLAYBACK_IS_PLAYING
import com.smascaro.trackmixing.common.SHARED_PREFERENCES_PLAYBACK_SONG_PLAYING
import com.smascaro.trackmixing.data.SharedPreferencesFactory
import com.smascaro.trackmixing.tracks.Track
import com.smascaro.trackmixing.ui.common.BaseObservableViewMvc
import com.smascaro.trackmixing.ui.common.UiUtils
import com.smascaro.trackmixing.ui.common.navigationhelper.NavigationHelper
import javax.inject.Inject

class BottomPlayerViewMvcImpl @Inject constructor(
    private val uiUtils: UiUtils,
    private val glide: RequestManager,
    private val navigationHelper: NavigationHelper
) :
    BaseObservableViewMvc<BottomPlayerViewMvc.Listener>(),
    BottomPlayerViewMvc,
    SharedPreferences.OnSharedPreferenceChangeListener {

    private lateinit var bottomBar: ConstraintLayout
    private lateinit var bottomBarTextView: MaterialTextView
    private lateinit var bottomBarBackgroundImageView: ImageView

    private var isBottomBarShown = false
    private val bottomBarHeight = uiUtils.DpToPixels(80f)

    private lateinit var sharedPreferences: SharedPreferences
    override fun bindRootView(rootView: View?) {
        super.bindRootView(rootView)
        initialize()
    }

    private fun initialize() {
        val bottomBarWrapper = findViewById<ConstraintLayout>(R.id.layout_player_actions_bottom)
        LayoutInflater.from(getContext())
            .inflate(R.layout.layout_actions_bottom, bottomBarWrapper, false)
        bottomBar = bottomBarWrapper.findViewById(R.id.layout_player_actions_bottom)
        bottomBarTextView = bottomBarWrapper.findViewById(R.id.tv_track_title_player_bottom)
        bottomBarBackgroundImageView =
            bottomBarWrapper.findViewById(R.id.iv_background_player_bottom)
        bottomBar.setOnClickListener {
            getListeners().forEach {
                it.onLayoutClick()
            }
        }
        sharedPreferences =
            SharedPreferencesFactory.getPlaybackSharedPreferencesFactory(getContext()!!)
        sharedPreferences.registerOnSharedPreferenceChangeListener(this)
    }

    override fun showPlayerBar(data: BottomPlayerData) {
        bottomBarTextView.text = data.title
        glide
            .asBitmap()
            .load(data.thumbnailUrl)
            .diskCacheStrategy(DiskCacheStrategy.AUTOMATIC)
            .into(BitmapImageViewTarget(bottomBarBackgroundImageView))
        bottomBar.visibility = View.VISIBLE
        val layoutParams = bottomBar.layoutParams
        layoutParams.height = bottomBarHeight.toInt()
        bottomBar.layoutParams = layoutParams
        isBottomBarShown = true
    }

    override fun hidePlayerBar() {
        val layoutParams = bottomBar.layoutParams
        layoutParams.height = 0
        bottomBar.layoutParams = layoutParams
        bottomBar.visibility = View.GONE
        isBottomBarShown = false
    }

    override fun navigateToPlayer(track: Track) {
        navigationHelper.toPlayer(track)
    }

    override fun onSharedPreferenceChanged(sharedPreferences: SharedPreferences?, key: String?) {
        if (key != null && key == SHARED_PREFERENCES_PLAYBACK_SONG_PLAYING || key == SHARED_PREFERENCES_PLAYBACK_IS_PLAYING) {
            getListeners().forEach {
                it.onPlayerStateChanged()
            }
        }
    }

}