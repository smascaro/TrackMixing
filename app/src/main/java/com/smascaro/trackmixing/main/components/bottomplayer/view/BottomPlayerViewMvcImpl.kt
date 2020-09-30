package com.smascaro.trackmixing.main.components.bottomplayer.view

import android.content.SharedPreferences
import android.view.LayoutInflater
import android.view.View
import android.view.animation.Animation
import android.view.animation.AnimationSet
import android.view.animation.DecelerateInterpolator
import android.widget.ImageView
import android.widget.TextSwitcher
import androidx.constraintlayout.motion.widget.MotionLayout
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
import timber.log.Timber
import javax.inject.Inject

class BottomPlayerViewMvcImpl @Inject constructor(
    private val serviceChecker: MixPlayerServiceChecker,
    private val resources: ResourcesWrapper
) :
    BaseObservableViewMvc<BottomPlayerViewMvc.Listener>(),
    BottomPlayerViewMvc,
    SharedPreferences.OnSharedPreferenceChangeListener {

    private lateinit var bottomBar: MaterialCardView
    private lateinit var bottomBarWrapper: MotionLayout
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

    private var currentMotionState: Int = R.id.player_hidden


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
        bottomBarWrapper = findViewById(R.id.motion_layout_main_activity)
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
//        val initialConstraintSet = bottomBarWrapper.transitionState.(R.id.player_hidden)
//        bottomBarWrapper.transitionToState(initialConstraintSet)
//        bottomBarWrapper.transitionToEnd()
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
//        bottomBarWrapper.setTransitionListener(object :TransitionAdapter(){
//            override fun onTransitionCompleted(motionLayout: MotionLayout?, currentId: Int) {
//                super.onTransitionCompleted(motionLayout, currentId)
//            }
//        })
        bottomBarWrapper.setTransitionListener(object : MotionLayout.TransitionListener {
            override fun onTransitionStarted(p0: MotionLayout?, p1: Int, p2: Int) {

            }

            override fun onTransitionChange(
                p0: MotionLayout?,
                beginState: Int,
                endState: Int,
                progress: Float
            ) {
                val beginStateName = getStateName(beginState)
                val endStateName = getStateName(endState)
                Timber.d("MotionLayout: onTransitionChange - from $beginStateName to $endStateName (${progress * 100f}%")
            }

            override fun onTransitionCompleted(motionLayout: MotionLayout?, currentId: Int) {
                val state = getStateName(currentId)
                Timber.d("MotionLayout: onTransitionCompleted - $state")
                when (currentId) {
                    R.id.swiped_out -> handleSwipeOut()
                }
//                currentMotionState = currentId
            }

            override fun onTransitionTrigger(p0: MotionLayout?, p1: Int, p2: Boolean, p3: Float) {
//                TODO("Not yet implemented")
            }

        })
//        bottomBarWrapper.setOnClickListener {
//            getListeners().forEach {
//                it.onLayoutClick()
//            }
////            bottomBarWrapper.transitionToState(R.id.fullscreen)
////            bottomBarWrapper.transitionToEnd()
//        }
//        val gestureListener = BottomPlayerFlingDetectorListener()
//        gestureListener.setOnFlingAction {
//            when (it) {
//                BottomPlayerFlingDetectorListener.FlingMode.LEFT_TO_RIGHT -> handleSwipeOut()
//                BottomPlayerFlingDetectorListener.FlingMode.BOTTOM_TO_TOP -> handleBottomToTopSwipe()
//                BottomPlayerFlingDetectorListener.FlingMode.NONE -> null
//            }
//        }
//        val gestureDetector = GestureDetector(getContext(), gestureListener)
//        val bottomPlayerTouchListener = BottomPlayerOnTouchListener(gestureDetector)
//        bottomBarWrapper.setOnTouchListener(bottomPlayerTouchListener)
    }

    private fun getStateName(currentId: Int): String {
        return when (currentId) {
            R.id.swiped_out -> "swiped_out"
            R.id.pre_swipe_out -> "pre_swipe_out"
            R.id.player_hidden -> "player_hidden"
            R.id.player_visible -> "player_visible"
            R.id.fullscreen -> "fullscreen"
            else -> "unkown"
        }
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
        isBottomBarShown = false
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
//                resetBottomBarDefaultPosition()
//                bottomBarWrapper.visibility = View.VISIBLE
                bottomBarTextSwitcher.setText(textToShow)
//                val animation = ResizeAnimation(bottomBarWrapper, bottomBarVisibleHeight.toInt()).apply {
//                    duration = inAnimationDuration
//                }
//                bottomBar.startAnimation(animation)
//                bottomBarWrapper.setState(R.id.player_hidden, getRootView().width,getRootView().height)
//                val initialConstraintSet = bottomBarWrapper.getConstraintSet(R.id.player_hidden)
//                bottomBarWrapper.updateState(R.id.player_hidden,initialConstraintSet)
                Timber.d("MotionLayout: Should transition to player_visible state")
                bottomBarWrapper.transitionToState(R.id.player_hidden)
                bottomBarWrapper.transitionToState(R.id.player_visible)
//                bottomBarWrapper.setTransition(R.id.player_hidden, R.id.player_visible)
                bottomBarWrapper.progress = 0f
                bottomBarWrapper.transitionToEnd()
//                bottomBarWrapper.requestLayout()
//                bottomBarWrapper.transitionToEnd()
            }
            isBottomBarShown = true
            currentShownData = data
        }
    }

    private fun shouldReloadBottomBar(data: BottomPlayerData) =
        !isBottomBarShown || currentShownData?.title != data.title || currentShownData?.author != data.author

    private fun isPlayerHidden(): Boolean {
        return currentMotionState == R.id.player_hidden
    }

    override fun hidePlayerBar(mode: HideBarMode) {
//        if (isBottomBarShown) {
//            when (mode) {
//                is HideBarMode.Vertical -> renderVerticalAnimation()
//                is HideBarMode.Sideway -> renderHorizontalAnimation()
//            }
//        }
    }

    private fun resetBottomBarDefaultPosition() {
        bottomBarWrapper.x = 0f
        bottomBarWrapper.layoutParams.height = bottomBarHiddenHeight.toInt()
        bottomBarWrapper.alpha = 1f
        bottomBarWrapper.requestLayout()
    }

    private fun renderHorizontalAnimation() {
        val animationSet = AnimationSet(true)
        val swipeAnimation = SwipeRightAnimation(bottomBarWrapper, getRootView()).apply {
            duration = outAnimationDuration
        }
        val resizeDownAnimation =
            ResizeAnimation(bottomBarWrapper, bottomBarHiddenHeight.toInt()).apply {
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
        bottomBarWrapper.startAnimation(animationSet)
    }

    private fun renderVerticalAnimation() {
        val animation = ResizeAnimation(bottomBarWrapper, bottomBarHiddenHeight.toInt()).apply {
            duration = outAnimationDuration
            interpolator = DecelerateInterpolator()
        }
        bottomBarWrapper.startAnimation(animation)
        makeBottomBarInvisible()
    }

    private fun makeBottomBarInvisible() {
        bottomBarWrapper.visibility = View.INVISIBLE
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
        val totalWidth = bottomBarWrapper.right - bottomBarWrapper.left
        //Set 1px as minimum width because 0 is interpreted as a weighted width => full width
        val progressWidth = (totalWidth * percentage).coerceAtLeast(1f)
        timestampProgressIndicatorView.layoutParams.width = progressWidth.toInt()
        timestampProgressIndicatorView.requestLayout()
    }
}