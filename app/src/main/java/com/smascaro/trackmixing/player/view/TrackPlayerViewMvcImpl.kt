package com.smascaro.trackmixing.player.view

import android.content.SharedPreferences
import android.widget.ImageView
import android.widget.TextSwitcher
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.view.children
import com.smascaro.trackmixing.R
import com.smascaro.trackmixing.base.time.Seconds
import com.smascaro.trackmixing.base.ui.architecture.view.BaseObservableViewMvc
import com.smascaro.trackmixing.base.utils.ResourcesWrapper
import com.smascaro.trackmixing.playback.model.TrackVolumeBundle
import com.smascaro.trackmixing.playback.service.MixPlayerServiceChecker
import com.smascaro.trackmixing.playback.utils.state.PlaybackStateManager
import com.smascaro.trackmixing.playback.utils.state.SharedPreferencesFactory
import com.smascaro.trackmixing.player.model.TrackPlayerData
import com.smascaro.trackmixing.player.view.TrackPlayerViewMvcImpl.MotionState.Initial
import javax.inject.Inject

class TrackPlayerViewMvcImpl @Inject constructor(
    private val serviceChecker: MixPlayerServiceChecker,
    private val resources: ResourcesWrapper
) :
    BaseObservableViewMvc<TrackPlayerViewMvc.Listener>(),
    TrackPlayerViewMvc,
    SharedPreferences.OnSharedPreferenceChangeListener {
    private sealed class MotionState {
        object Initial : MotionState()
        object PlayerVisible : MotionState()
        object FullscreenPlayer : MotionState()
        object PreSwipeOut : MotionState()
        object SwipedOut : MotionState()
    }

    //Views
    private lateinit var bottomBar: ConstraintLayout
    private lateinit var bottomBarTextSwitcher: TextSwitcher
    private lateinit var bottomBarActionButton: ImageView

    //Private fields
    private var isBottomBarShown = false
    private val bottomBarVisibleHeight =
        resources.getDimension(R.dimen.actions_bottom_layout_visible_height)
    private val bottomBarHiddenHeight =
        resources.getDimension(R.dimen.actions_bottom_layout_hidden_height)
    private val inAnimationDuration =
        resources.getLong(R.integer.animation_slide_in_bottom_duration)
    private val outAnimationDuration =
        resources.getLong(R.integer.animation_slide_out_top_duration)
    private var currentShownData: TrackPlayerData? = null
    private lateinit var sharedPreferences: SharedPreferences
    private var currentMotionState: MotionState = Initial
    private var blockTimestampUpdates: Boolean = false

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
        super.initialize()
        bottomBar = findViewById(R.id.layout_bottom_player)
        bottomBarTextSwitcher = findViewById(R.id.tv_track_title_player_bottom)
        bottomBarActionButton = findViewById(R.id.iv_action_button_player_bottom)

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

        initializeProgressSeekBarListener()

        bottomBar.setOnClickListener {
            getListeners().forEach { it.onLayoutClick() }
        }
    }

    private fun initializeProgressSeekBarListener() {
        val progressChangeListener = ProgressSeekBarChangeListener()

        progressChangeListener.setOnStartTrackingTouch {
            blockTimestampUpdates = true
        }

        progressChangeListener.setOnStopTrackingTouch { seekBar ->
            if (seekBar != null) {
                blockTimestampUpdates = false
                getListeners().forEach {
                    it.onSeekRequestEvent(seekBar.progress)
                }
            }
        }

    }

    private fun initializeMarquee() {
        bottomBarTextSwitcher.children.forEach { it.isSelected = true }
    }

    override fun bindTrackDuration(length: Seconds) {}

    override fun bindVolumes(
        volumes: TrackVolumeBundle
    ) {
        /* nothing to do */
    }

    override fun onBackPressed(): Boolean {
        return false
    }

    private fun setupSharedPreferences() {
        sharedPreferences =
            SharedPreferencesFactory.getPlaybackSharedPreferencesFactory(
                getContext()!!
            )
        sharedPreferences.registerOnSharedPreferenceChangeListener(this)
    }

    override fun showPlayerBar(data: TrackPlayerData) {
        if (shouldReloadBottomBar(data)) {
            val textToShow =
                resources.getString(R.string.player_bottom_title, data.title, data.author)
            if (isBottomBarShown) {
                bottomBarTextSwitcher.setText(textToShow)
            } else {
                bottomBarTextSwitcher.setText(textToShow)
                bottomBar.layoutParams = (bottomBar.layoutParams as ConstraintLayout.LayoutParams).apply {
                    topToBottom = ConstraintLayout.LayoutParams.UNSET
                    bottomToBottom = ConstraintLayout.LayoutParams.PARENT_ID
                }
            }
            isBottomBarShown = true
            currentShownData = data
        }
    }

    private fun shouldReloadBottomBar(data: TrackPlayerData) =
        !isBottomBarShown || currentShownData?.title != data.title || currentShownData?.author != data.author

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

    override fun updateTimestamp(newTimestamp: Int, totalLength: Int) {
        /* nothing to do */
    }
}