package com.smascaro.trackmixing.main.components.bottomplayer.view

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
import com.smascaro.trackmixing.common.data.model.Track
import com.smascaro.trackmixing.common.utils.*
import com.smascaro.trackmixing.common.view.architecture.BaseObservableViewMvc
import com.smascaro.trackmixing.main.components.bottomplayer.model.BottomPlayerData
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
    private lateinit var bottomBarActionButton: ImageView

    private var isBottomBarShown = false
    private val bottomBarHeight = uiUtils.DpToPixels(80f)

    private var currentShownData: BottomPlayerData? = null
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
        bottomBarActionButton = bottomBarWrapper.findViewById(R.id.iv_action_button_player_bottom)
        bottomBarActionButton.setOnClickListener {
            getListeners().forEach {
                it.onActionButtonClicked()
            }
        }
        bottomBar.setOnClickListener {
            getListeners().forEach {
                it.onLayoutClick()
            }
        }
        setupSharedPreferences()
    }

    private fun setupSharedPreferences() {
        sharedPreferences =
            SharedPreferencesFactory.getPlaybackSharedPreferencesFactory(getContext()!!)
        sharedPreferences.registerOnSharedPreferenceChangeListener(this)
    }

    override fun showPlayerBar(data: BottomPlayerData) {
        if (shouldReloadBottomBar(data)) {
            bottomBarTextView.text = data.title
            renderBottomBarBackground(data.thumbnailUrl)
            setBottomBarVisibility(View.VISIBLE)
            setBottomBarHeight(bottomBarHeight.toInt())
            isBottomBarShown = true
            currentShownData = data
        }
    }

    private fun shouldReloadBottomBar(data: BottomPlayerData) =
        !isBottomBarShown || currentShownData?.title != data.title || currentShownData?.thumbnailUrl != data.thumbnailUrl

    private fun renderBottomBarBackground(imageUrl: String) {
        glide
            .asBitmap()
            .load(imageUrl)
            .diskCacheStrategy(DiskCacheStrategy.AUTOMATIC)
            .into(BitmapImageViewTarget(bottomBarBackgroundImageView))
    }

    private fun setBottomBarVisibility(visibility: Int) {
        bottomBar.visibility = visibility
    }

    private fun setBottomBarHeight(height: Int) {
        val layoutParams = bottomBar.layoutParams
        layoutParams.height = height
        bottomBar.layoutParams = layoutParams
    }

    override fun hidePlayerBar() {
        if (isBottomBarShown) {
            setBottomBarHeight(0)
            setBottomBarVisibility(View.GONE)
            isBottomBarShown = false
        }
    }

    override fun showPlayButton() {
        bottomBarActionButton.setImageResource(R.drawable.ic_play)
    }

    override fun showPauseButton() {
        bottomBarActionButton.setImageResource(R.drawable.ic_pause)
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