package com.smascaro.trackmixing.player.view

import android.content.SharedPreferences
import android.view.View
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.SeekBar
import android.widget.TextSwitcher
import androidx.constraintlayout.motion.widget.MotionLayout
import androidx.core.view.children
import com.google.android.material.textview.MaterialTextView
import com.smascaro.trackmixing.R
import com.smascaro.trackmixing.base.time.Seconds
import com.smascaro.trackmixing.base.time.TimeHelper
import com.smascaro.trackmixing.base.ui.architecture.view.BaseObservableViewMvc
import com.smascaro.trackmixing.base.ui.widget.PivotableSeekbar
import com.smascaro.trackmixing.base.utils.ResourcesWrapper
import com.smascaro.trackmixing.playback.service.MixPlayerServiceChecker
import com.smascaro.trackmixing.player.model.TrackPlayerData
import com.smascaro.trackmixing.player.view.TrackPlayerViewMvcImpl.MotionState.FullscreenPlayer
import com.smascaro.trackmixing.player.view.TrackPlayerViewMvcImpl.MotionState.Initial
import com.smascaro.trackmixing.player.view.TrackPlayerViewMvcImpl.MotionState.PlayerVisible
import com.smascaro.trackmixing.player.view.TrackPlayerViewMvcImpl.MotionState.PreSwipeOut
import com.smascaro.trackmixing.player.view.TrackPlayerViewMvcImpl.MotionState.SwipedOut
import timber.log.Timber
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
    private lateinit var bottomBar: FrameLayout
    private lateinit var motionLayout: MotionLayout
    private lateinit var bottomBarTextSwitcher: TextSwitcher
    private lateinit var bottomBarActionButton: ImageView
    private lateinit var timestampProgressIndicatorView: View
    private lateinit var currentTimestampTextView: MaterialTextView
    private lateinit var totalLengthTextView: MaterialTextView
    private lateinit var songProgressSeekbar: SeekBar

    //SeekBars
    private lateinit var vocalsVolumeSeekbar: PivotableSeekbar
    private lateinit var otherVolumeSeekbar: PivotableSeekbar
    private lateinit var bassVolumeSeekbar: PivotableSeekbar
    private lateinit var drumsVolumeSeekbar: PivotableSeekbar

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
        motionLayout = findViewById(R.id.motion_layout_main_activity)
        bottomBar = motionLayout.findViewById(R.id.layout_player)
        bottomBarTextSwitcher = motionLayout.findViewById(R.id.tv_track_title_player_bottom)
        bottomBarActionButton = motionLayout.findViewById(R.id.iv_action_button_player_bottom)
        timestampProgressIndicatorView =
            motionLayout.findViewById(R.id.v_bottom_player_progress_indicator)

        bottomBarTextSwitcher.isSelected = true
        bottomBarTextSwitcher.setInAnimation(getContext(), R.anim.slide_in_right)
        bottomBarTextSwitcher.setOutAnimation(getContext(), R.anim.slide_out_left)

        currentTimestampTextView = findViewById(R.id.tv_track_player_current_timestamp)
        totalLengthTextView = findViewById(R.id.tv_track_player_length)
        songProgressSeekbar = findViewById(R.id.sb_track_player_timestamp)

        vocalsVolumeSeekbar = findViewById(R.id.sb_track_player_vocals)
        otherVolumeSeekbar = findViewById(R.id.sb_track_player_other)
        bassVolumeSeekbar = findViewById(R.id.sb_track_player_bass)
        drumsVolumeSeekbar = findViewById(R.id.sb_track_player_drums)

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

        vocalsVolumeSeekbar.setOnSeekBarChangeListener(makeSeekbarChangeListenerFor(com.smascaro.trackmixing.playback.model.TrackInstrument.VOCALS))
        otherVolumeSeekbar.setOnSeekBarChangeListener(makeSeekbarChangeListenerFor(com.smascaro.trackmixing.playback.model.TrackInstrument.OTHER))
        bassVolumeSeekbar.setOnSeekBarChangeListener(makeSeekbarChangeListenerFor(com.smascaro.trackmixing.playback.model.TrackInstrument.BASS))
        drumsVolumeSeekbar.setOnSeekBarChangeListener(makeSeekbarChangeListenerFor(com.smascaro.trackmixing.playback.model.TrackInstrument.DRUMS))

        initializeMotionLayoutListener()

        initializeProgressSeekBarListener()
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
                val state = getStateName(currentId)
                Timber.d("MotionLayout: onTransitionCompleted - $state")
                when (currentId) {
                    R.id.swiped_out -> handleSwipeOut()
                }
                currentMotionState = getMotionStateByResourceId(currentId)
            }

            override fun onTransitionTrigger(p0: MotionLayout?, p1: Int, p2: Boolean, p3: Float) {
                // Do nothing
            }
        })
    }

    private fun getMotionStateByResourceId(id: Int): MotionState {
        return when (id) {
            R.id.player_hidden -> Initial
            R.id.player_visible -> PlayerVisible
            R.id.fullscreen -> FullscreenPlayer
            R.id.pre_swipe_out -> PreSwipeOut
            R.id.swiped_out -> SwipedOut
            else -> Initial
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

        progressChangeListener.setOnProgressChanged { _, progress, _ ->
            currentTimestampTextView.text = transformSecondsToTimeRepresentation(progress)
        }
        songProgressSeekbar.setOnSeekBarChangeListener(progressChangeListener)
    }

    private fun makeSeekbarChangeListenerFor(trackInstrument: com.smascaro.trackmixing.playback.model.TrackInstrument): TrackMixerSeekBarChangeListener {
        return TrackMixerSeekBarChangeListener { progress ->
            getListeners().forEach { listener ->
                listener.onTrackVolumeChanged(trackInstrument, progress)
            }
        }
    }

    private fun getStateName(currentId: Int): String {
        return when (currentId) {
            R.id.swiped_out -> "swiped_out"
            R.id.pre_swipe_out -> "pre_swipe_out"
            R.id.player_hidden -> "player_hidden"
            R.id.player_visible -> "player_visible"
            R.id.fullscreen -> "fullscreen"
            else -> "unknown"
        }
    }

    private fun initializeMarquee() {
        bottomBarTextSwitcher.children.forEach { it.isSelected = true }
    }

    override fun bindTrackDuration(length: Seconds) {
        totalLengthTextView.text =
            transformSecondsToTimeRepresentation(length.value.toInt())
        songProgressSeekbar.max = length.value.toInt()
    }

    override fun bindVolumes(
        volumes: com.smascaro.trackmixing.playback.utils.TrackVolumeBundle
    ) {
        vocalsVolumeSeekbar.progress = volumes.vocals
        otherVolumeSeekbar.progress = volumes.other
        bassVolumeSeekbar.progress = volumes.bass
        drumsVolumeSeekbar.progress = volumes.drums
    }

    override fun openPlayer() {
        motionLayout.transitionToState(R.id.player_visible)
        motionLayout.setTransition(R.id.open_player)
        motionLayout.transitionToEnd()
    }

    override fun onBackPressed(): Boolean {
        return if (motionLayout.currentState == R.id.fullscreen) {
            motionLayout.setTransition(R.id.open_player)
            motionLayout.transitionToStart()
            true
        } else {
            false
        }
    }

    private fun transformSecondsToTimeRepresentation(seconds: Int): String {
        return TimeHelper.fromSeconds(seconds.toLong()).toStringRepresentation()
    }

    private fun handleSwipeOut() {
        getListeners().forEach {
            it.onPlayerSwipedOut()
        }
        isBottomBarShown = false
    }

    private fun setupSharedPreferences() {
        sharedPreferences =
            com.smascaro.trackmixing.playback.utils.SharedPreferencesFactory.getPlaybackSharedPreferencesFactory(
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

    private fun shouldReloadBottomBar(data: TrackPlayerData) =
        !isBottomBarShown || currentShownData?.title != data.title || currentShownData?.author != data.author

    override fun showPlayButton() {
        bottomBarActionButton.setImageResource(R.drawable.ic_play)
    }

    override fun showPauseButton() {
        bottomBarActionButton.setImageResource(R.drawable.ic_pause)
    }

    override fun onSharedPreferenceChanged(sharedPreferences: SharedPreferences?, key: String?) {
        if (key != null && key == com.smascaro.trackmixing.playback.utils.PlaybackStateManager.SHARED_PREFERENCES_PLAYBACK_SONG_PLAYING || key == com.smascaro.trackmixing.playback.utils.PlaybackStateManager.SHARED_PREFERENCES_PLAYBACK_IS_PLAYING) {
            getListeners().forEach {
                it.onPlayerStateChanged()
            }
        }
    }

    override fun updateTimestamp(newTimestamp: Int, totalLength: Int) {
        val percentage = (newTimestamp.toDouble() / totalLength.toDouble())
        val totalWidth = motionLayout.right - motionLayout.left
        //Set 1px as minimum width because 0 is interpreted as a weighted width => full width
        val progressWidth = (totalWidth * percentage).coerceAtLeast(1.0)
        timestampProgressIndicatorView.layoutParams.width = progressWidth.toInt()

        if (currentMotionState is FullscreenPlayer && !blockTimestampUpdates) {
            currentTimestampTextView.text =
                transformSecondsToTimeRepresentation(newTimestamp)
            songProgressSeekbar.progress = newTimestamp
        }
    }
}