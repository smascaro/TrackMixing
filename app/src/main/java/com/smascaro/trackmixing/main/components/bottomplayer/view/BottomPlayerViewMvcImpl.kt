package com.smascaro.trackmixing.main.components.bottomplayer.view

//import com.smascaro.trackmixing.common.di.PlaybackSharedPreferences
import android.content.SharedPreferences
import android.view.GestureDetector
import android.view.LayoutInflater
import android.view.View
import android.view.animation.Animation
import android.widget.ImageView
import android.widget.Toast
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
import com.smascaro.trackmixing.main.components.progress.view.SwipeRightAnimation
import com.smascaro.trackmixing.playbackservice.MixPlayerServiceChecker
import javax.inject.Inject

class BottomPlayerViewMvcImpl @Inject constructor(
    private val glide: RequestManager,
    private val resources: ResourcesWrapper,
    private val serviceChecker: MixPlayerServiceChecker
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
        val isServiceRunning = isServiceRunning()
        getListeners().forEach {
            it.onServiceRunningCheck(isServiceRunning)
        }
    }

    fun isServiceRunning(): Boolean {
        return serviceChecker.ping()
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
        bottomBarActionButton = bottomBarWrapper.findViewById(R.id.iv_action_button_player_bottom)
        timestampProgressIndicatorView =
            bottomBarWrapper.findViewById(R.id.v_bottom_player_progress_indicator)

        bottomBarTextView.isSelected = true
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
        val gestureListener = BottomPlayerFlingDetectorListener()
        gestureListener.setOnFlingAction {
            when (it) {
                BottomPlayerFlingDetectorListener.FlingMode.LEFT_TO_RIGHT -> handleSwipeOut()
                BottomPlayerFlingDetectorListener.FlingMode.BOTTOM_TO_TOP -> handleBottomToTopSwipe()
                BottomPlayerFlingDetectorListener.FlingMode.NONE -> null
            }
        }
        val gestureDetector = GestureDetector(getContext(), gestureListener)
        bottomBar.setOnTouchListener { v, event ->
            gestureDetector.onTouchEvent(event)
            return@setOnTouchListener true
        }
        setupSharedPreferences()
    }

    private fun handleBottomToTopSwipe() {
        getListeners().forEach {
            it.onSwipeUp()
        }
    }

    private fun handleSwipeOut() {
        getListeners().forEach {
            it.onSwipeRight()
        }
    }

    private fun showToast(message: String) {
        Toast.makeText(getContext(), message, Toast.LENGTH_SHORT).show()
    }

    private fun setupSharedPreferences() {
        sharedPreferences =
            SharedPreferencesFactory.getPlaybackSharedPreferencesFactory(getContext()!!)
        sharedPreferences.registerOnSharedPreferenceChangeListener(this)
    }

    override fun showPlayerBar(data: BottomPlayerData) {
        if (shouldReloadBottomBar(data)) {
            resetBottomBarDefaultPosition()
            bottomBar.visibility = View.VISIBLE
            if (bottomBarTextView.text != data.title) {
                bottomBarTextView.text = data.title
            }
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


    override fun hidePlayerBar(mode: HideBarMode) {
        if (isBottomBarShown) {
            val animation = when (mode) {
                is HideBarMode.Vertical -> getVerticalAnimation()
                is HideBarMode.Sideway -> getHorizontalAnimation()
            }
            bottomBar.startAnimation(animation)
            resetBottomBarDefaultPosition()
            bottomBar.visibility = View.INVISIBLE
            isBottomBarShown = false
        }
    }

    private fun resetBottomBarDefaultPosition() {
        bottomBar.x = 0f
        bottomBar.layoutParams.height = bottomBarHiddenHeight.toInt()
        bottomBar.requestLayout()
    }

    private fun getHorizontalAnimation(): Animation {
        return SwipeRightAnimation(bottomBar, getRootView()).apply {
            duration = 150
        }
    }

    private fun getVerticalAnimation(): Animation {
        return ResizeAnimation(bottomBar, bottomBarHiddenHeight.toInt()).apply {
            duration = outAnimationDuration
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