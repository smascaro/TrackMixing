package com.smascaro.trackmixing.main.components.bottomplayer.view

import android.content.SharedPreferences
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.TextSwitcher
import androidx.constraintlayout.motion.widget.MotionLayout
import androidx.core.view.children
import com.smascaro.trackmixing.R
import com.smascaro.trackmixing.base.ui.architecture.view.BaseObservableViewMvc
import com.smascaro.trackmixing.base.utils.ResourcesWrapper
import com.smascaro.trackmixing.playback.service.MixPlayerServiceChecker
import com.smascaro.trackmixing.playback.utils.state.PlaybackStateManager
import com.smascaro.trackmixing.playback.utils.state.SharedPreferencesFactory
import timber.log.Timber
import javax.inject.Inject

class BottomPlayerViewMvcImpl @Inject constructor(
    private val serviceChecker: MixPlayerServiceChecker,
    private val resources: ResourcesWrapper
) : BaseObservableViewMvc<BottomPlayerViewMvc.Listener>(), BottomPlayerViewMvc,
    SharedPreferences.OnSharedPreferenceChangeListener {
    private sealed class MotionState {
        object Initial : MotionState()
        object PlayerVisible : MotionState()
        object FullscreenPlayer : MotionState()
        object PreSwipeOut : MotionState()
        object SwipedOut : MotionState()
    }
    //Views
    private lateinit var bottomBar: FrameLayout
    private lateinit var motionLayout: MotionLayout
    private lateinit var bottomBarTextSwitcher: TextSwitcher
    private lateinit var bottomBarActionButton: ImageView

    private lateinit var sharedPreferences: SharedPreferences
    private var currentMotionState: MotionState = MotionState.Initial
    private var isBottomBarShown = false
    private var currentShownData: com.smascaro.trackmixing.player.model.TrackPlayerData? = null

    override fun onCreate() {
        val isServiceRunning = isServiceRunning()
        getListeners().forEach {
            it.onServiceRunningCheck(isServiceRunning)
        }
    }

    private fun isServiceRunning(): Boolean {
        return serviceChecker.ping()
    }

    override fun initialize() {
        motionLayout = findViewById(R.id.motion_layout_main_activity)
        bottomBar = motionLayout.findViewById(R.id.layout_player)
        bottomBarTextSwitcher = motionLayout.findViewById(R.id.tv_track_title_player_bottom)
        bottomBarActionButton = motionLayout.findViewById(R.id.iv_action_button)

        bottomBarTextSwitcher.isSelected = true
        bottomBarTextSwitcher.setInAnimation(getContext(), R.anim.slide_in_right)
        bottomBarTextSwitcher.setOutAnimation(getContext(), R.anim.slide_out_left)

        initializeMarquee()
        setupSharedPreferences()
    }

    private fun initializeMarquee() {
        bottomBarTextSwitcher.children.forEach { it.isSelected = true }
    }

    private fun setupSharedPreferences() {
        sharedPreferences =
            SharedPreferencesFactory.getPlaybackSharedPreferencesFactory(
                getContext()!!
            )
        sharedPreferences.registerOnSharedPreferenceChangeListener(this)
    }

    override fun initializeListeners() {
        super.initializeListeners()
        bottomBarActionButton.setOnClickListener {
            getListeners().forEach {
                it.onActionButtonClicked()
            }
        }
        bottomBar.setOnClickListener {
            getListeners().forEach { it.onBottomPlayerClick() }
        }
        initializeMotionLayoutListener()
    }

    private fun initializeMotionLayoutListener() {
        motionLayout.setTransitionListener(object : MotionLayout.TransitionListener {
            override fun onTransitionStarted(p0: MotionLayout?, p1: Int, p2: Int) {
                // Do nothing
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
            }

            override fun onTransitionTrigger(p0: MotionLayout?, p1: Int, p2: Boolean, p3: Float) {
                // Do nothing
            }
        })
    }

    private fun getStateName(currentId: Int): String {
        return when (currentId) {
            R.id.player_hidden -> "player_hidden"
            R.id.player_visible -> "player_visible"
            else -> "unknown"
        }
    }

    override fun showPlayerBar(data: com.smascaro.trackmixing.player.model.TrackPlayerData) {
        if (shouldReloadBottomBar(data)) {
            val textToShow =
                resources.getString(R.string.player_bottom_title, data.title, data.author)
            if (isBottomBarShown) {
                bottomBarTextSwitcher.setText(textToShow)
            } else {
                bottomBarTextSwitcher.setText(textToShow)
                Timber.d("MotionLayout: Should transition to player_visible state")
                motionLayout.transitionToState(R.id.player_hidden)
                motionLayout.transitionToState(R.id.player_visible)
                motionLayout.progress = 0f
                motionLayout.transitionToEnd()
            }
            isBottomBarShown = true
            currentShownData = data
        }
    }

    private fun shouldReloadBottomBar(data: com.smascaro.trackmixing.player.model.TrackPlayerData) =
        !isBottomBarShown || currentShownData?.title != data.title || currentShownData?.author != data.author

    override fun hidePlayerBar() {
        motionLayout.transitionToState(R.id.player_visible)
        motionLayout.progress = 1f
        motionLayout.transitionToStart()
        isBottomBarShown = false
    }

    override fun showPlayButton() {
        bottomBarActionButton.setImageResource(R.drawable.ic_play)
    }

    override fun showPauseButton() {
        bottomBarActionButton.setImageResource(R.drawable.ic_pause)
    }

    override fun onSharedPreferenceChanged(sharedPreferences: SharedPreferences?, key: String?) {
        if (key != null && key == PlaybackStateManager.SHARED_PREFERENCES_PLAYBACK_SONG_PLAYING || key == PlaybackStateManager.SHARED_PREFERENCES_PLAYBACK_IS_PLAYING) {
            getListeners().forEach {
                it.onPlayerStateChanged()
            }
        }
    }
}