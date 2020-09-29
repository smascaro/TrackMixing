package com.smascaro.trackmixing.main.components.bottomplayer.view

import android.content.SharedPreferences
import android.view.GestureDetector
import android.view.LayoutInflater
import android.view.View
import android.view.animation.Animation
import android.view.animation.AnimationSet
import android.view.animation.DecelerateInterpolator
import android.widget.ImageView
import android.widget.TextSwitcher
import androidx.core.view.children
import com.google.android.material.card.MaterialCardView
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
    private val serviceChecker: MixPlayerServiceChecker,
    private val resources: ResourcesWrapper
) :
    BaseObservableViewMvc<BottomPlayerViewMvc.Listener>(),
    BottomPlayerViewMvc,
    SharedPreferences.OnSharedPreferenceChangeListener {

    private lateinit var bottomBar: MaterialCardView
    private lateinit var bottomBarTextSwitcher: TextSwitcher

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

    override fun initialize() {
        super.initialize()
        val bottomBarWrapper = findViewById<MaterialCardView>(R.id.layout_player_actions_bottom)
        LayoutInflater.from(getContext())
            .inflate(R.layout.layout_actions_bottom, bottomBarWrapper, false)
        bottomBar = bottomBarWrapper.findViewById(R.id.layout_player_actions_bottom)
        bottomBarTextSwitcher = bottomBarWrapper.findViewById(R.id.tv_track_title_player_bottom)
        bottomBarActionButton = bottomBarWrapper.findViewById(R.id.iv_action_button_player_bottom)
        timestampProgressIndicatorView =
            bottomBarWrapper.findViewById(R.id.v_bottom_player_progress_indicator)

        bottomBarTextSwitcher.isSelected = true
        bottomBarTextSwitcher.setInAnimation(getContext(), R.anim.slide_in_right)
        bottomBarTextSwitcher.setOutAnimation(getContext(), R.anim.slide_out_left)
        initializeMarquee()
        setupSharedPreferences()
    }

    override fun initializeListeners() {
        super.initializeListeners()
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
        val bottomPlayerTouchListener = BottomPlayerOnTouchListener(gestureDetector)
        bottomBar.setOnTouchListener(bottomPlayerTouchListener)
    }

    private fun initializeMarquee() {
        bottomBarTextSwitcher.children.forEach { it.isSelected = true }
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

    private fun setupSharedPreferences() {
        sharedPreferences =
            SharedPreferencesFactory.getPlaybackSharedPreferencesFactory(getContext()!!)
        sharedPreferences.registerOnSharedPreferenceChangeListener(this)
    }

    override fun showPlayerBar(data: BottomPlayerData) {
        if (shouldReloadBottomBar(data)) {
            val textToShow =
                resources.getString(R.string.player_bottom_title, data.title, data.author)
            if (isBottomBarShown) {
                bottomBarTextSwitcher.setText(textToShow)
            } else {
                resetBottomBarDefaultPosition()
                bottomBar.visibility = View.VISIBLE
                bottomBarTextSwitcher.setText(textToShow)
                val animation = ResizeAnimation(bottomBar, bottomBarVisibleHeight.toInt()).apply {
                    duration = inAnimationDuration
                }
                bottomBar.startAnimation(animation)
            }
            isBottomBarShown = true
            currentShownData = data
        }
    }

    private fun shouldReloadBottomBar(data: BottomPlayerData) =
        !isBottomBarShown || currentShownData?.title != data.title || currentShownData?.author != data.author

    override fun hidePlayerBar(mode: HideBarMode) {
        if (isBottomBarShown) {
            when (mode) {
                is HideBarMode.Vertical -> renderVerticalAnimation()
                is HideBarMode.Sideway -> renderHorizontalAnimation()
            }
        }
    }

    private fun resetBottomBarDefaultPosition() {
        bottomBar.x = 0f
        bottomBar.layoutParams.height = bottomBarHiddenHeight.toInt()
        bottomBar.alpha = 1f
        bottomBar.requestLayout()
    }

    private fun renderHorizontalAnimation() {
        val animationSet = AnimationSet(true)
        val swipeAnimation = SwipeRightAnimation(bottomBar, getRootView()).apply {
            duration = outAnimationDuration
        }
        val resizeDownAnimation = ResizeAnimation(bottomBar, bottomBarHiddenHeight.toInt()).apply {
            duration = outAnimationDuration
            startOffset = outAnimationDuration
            interpolator = DecelerateInterpolator()
        }
        animationSet.addAnimation(swipeAnimation)
        animationSet.addAnimation(resizeDownAnimation)
        animationSet.setAnimationListener(object : Animation.AnimationListener {
            override fun onAnimationRepeat(animation: Animation?) {}

            override fun onAnimationEnd(animation: Animation?) {
                makeBottomBarInvisible()
            }

            override fun onAnimationStart(animation: Animation?) {}
        })
        bottomBar.startAnimation(animationSet)
    }

    private fun renderVerticalAnimation() {
        val animation = ResizeAnimation(bottomBar, bottomBarHiddenHeight.toInt()).apply {
            duration = outAnimationDuration
            interpolator = DecelerateInterpolator()
        }
        bottomBar.startAnimation(animation)
        makeBottomBarInvisible()
    }

    private fun makeBottomBarInvisible() {
        bottomBar.visibility = View.INVISIBLE
        isBottomBarShown = false
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