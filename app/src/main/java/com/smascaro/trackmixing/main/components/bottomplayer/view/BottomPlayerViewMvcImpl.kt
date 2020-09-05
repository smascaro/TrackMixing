package com.smascaro.trackmixing.main.components.bottomplayer.view

//import com.smascaro.trackmixing.common.di.PlaybackSharedPreferences
import android.content.SharedPreferences
import android.view.LayoutInflater
import android.view.View
import android.widget.ImageView
import com.bumptech.glide.RequestManager
import com.google.android.material.card.MaterialCardView
import com.google.android.material.textview.MaterialTextView
import com.smascaro.trackmixing.R
import com.smascaro.trackmixing.common.utils.ResourcesWrapper
import com.smascaro.trackmixing.common.utils.SHARED_PREFERENCES_PLAYBACK_IS_PLAYING
import com.smascaro.trackmixing.common.utils.SHARED_PREFERENCES_PLAYBACK_SONG_PLAYING
import com.smascaro.trackmixing.common.utils.SharedPreferencesFactory
import com.smascaro.trackmixing.common.view.architecture.BaseObservableViewMvc
import com.smascaro.trackmixing.main.components.bottomplayer.model.BottomPlayerData
import com.smascaro.trackmixing.main.components.progress.view.ResizeAnimation
import com.smascaro.trackmixing.playbackservice.MixPlayerService
import javax.inject.Inject

class BottomPlayerViewMvcImpl @Inject constructor(
    private val glide: RequestManager,
    private val resources: ResourcesWrapper
) :
    BaseObservableViewMvc<BottomPlayerViewMvc.Listener>(),
    BottomPlayerViewMvc,
    SharedPreferences.OnSharedPreferenceChangeListener {

    private lateinit var bottomBar: MaterialCardView
    private lateinit var bottomBarTextView: MaterialTextView

    //    private lateinit var bottomBarBackgroundImageView: ImageView
    private lateinit var bottomBarActionButton: ImageView
    private lateinit var timestampProgressIndicatorView: View

    private var isBottomBarShown = false
    private val bottomBarVisibleHeight =
        resources.getDimension(R.dimen.actions_bottom_layout_visible_height)
    private val bottomBarHiddenHeight =
        resources.getDimension(R.dimen.actions_bottom_layout_hidden_height)
    private val inAnimationDuration =
        resources.getLong(R.integer.animation_slide_in_bottom_duration)
    private val outAnimationDuration =
        resources.getLong(R.integer.animation_slide_out_top_duration)
    private var currentShownData: BottomPlayerData? = null
    private lateinit var sharedPreferences: SharedPreferences

    override fun onCreate() {
        val isServiceRunning = MixPlayerService.ping(getContext()!!)
        getListeners().forEach {
            it.onServiceRunningCheck(isServiceRunning)
        }
    }

    override fun bindRootView(rootView: View?) {
        super.bindRootView(rootView)
        initialize()
    }

    private fun initialize() {
        val bottomBarWrapper = findViewById<MaterialCardView>(R.id.layout_player_actions_bottom)
        LayoutInflater.from(getContext())
            .inflate(R.layout.layout_actions_bottom, bottomBarWrapper, false)
        bottomBar = bottomBarWrapper.findViewById(R.id.layout_player_actions_bottom)
        bottomBarTextView = bottomBarWrapper.findViewById(R.id.tv_track_title_player_bottom)
//        bottomBarBackgroundImageView =
//            bottomBarWrapper.findViewById(R.id.iv_background_player_bottom)
        bottomBarActionButton = bottomBarWrapper.findViewById(R.id.iv_action_button_player_bottom)
        timestampProgressIndicatorView =
            bottomBarWrapper.findViewById(R.id.v_bottom_player_progress_indicator)
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
            bottomBar.visibility = View.VISIBLE
            bottomBarTextView.text = data.title
            renderBottomBarBackground(data.thumbnailUrl)
            val animation = ResizeAnimation(bottomBar, bottomBarVisibleHeight.toInt()).apply {
                duration = inAnimationDuration
            }
            bottomBar.startAnimation(animation)
            isBottomBarShown = true
            currentShownData = data
        }
    }

    private fun shouldReloadBottomBar(data: BottomPlayerData) =
        !isBottomBarShown || currentShownData?.title != data.title || currentShownData?.thumbnailUrl != data.thumbnailUrl

    private fun renderBottomBarBackground(imageUrl: String) {
//        glide
//            .asBitmap()
//            .load(imageUrl)
//            .diskCacheStrategy(DiskCacheStrategy.AUTOMATIC)
//            .into(BitmapImageViewTarget(bottomBarBackgroundImageView))
    }

    override fun hidePlayerBar() {
        if (isBottomBarShown) {
            val animation = ResizeAnimation(bottomBar, bottomBarHiddenHeight.toInt()).apply {
                duration = outAnimationDuration
            }
            bottomBar.startAnimation(animation)
            bottomBar.visibility = View.INVISIBLE
            isBottomBarShown = false
        }
    }

    override fun showPlayButton() {
        bottomBarActionButton.setImageResource(R.drawable.ic_play)
    }

    override fun showPauseButton() {
        bottomBarActionButton.setImageResource(R.drawable.ic_pause)
    }


    override fun onSharedPreferenceChanged(sharedPreferences: SharedPreferences?, key: String?) {
        if (key != null && key == SHARED_PREFERENCES_PLAYBACK_SONG_PLAYING || key == SHARED_PREFERENCES_PLAYBACK_IS_PLAYING) {
            getListeners().forEach {
                it.onPlayerStateChanged()
            }
        }
    }

    override fun updateTimestamp(percentage: Float) {
        val totalWidth = bottomBar.right - bottomBar.left
        //Set 1px as minimum width because 0 is interpreted as a weighted width => full width
        val progressWidth = (totalWidth * percentage).coerceAtLeast(1f)
        timestampProgressIndicatorView.layoutParams.width = progressWidth.toInt()
        timestampProgressIndicatorView.requestLayout()
    }
}