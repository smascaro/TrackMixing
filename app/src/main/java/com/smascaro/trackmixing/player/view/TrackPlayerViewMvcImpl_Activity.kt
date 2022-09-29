package com.smascaro.trackmixing.player.view

import android.content.SharedPreferences
import android.widget.ImageButton
import android.widget.SeekBar
import android.widget.TextView
import com.smascaro.trackmixing.R
import com.smascaro.trackmixing.base.time.Seconds
import com.smascaro.trackmixing.base.time.TimeHelper
import com.smascaro.trackmixing.base.ui.architecture.view.BaseObservableViewMvc
import com.smascaro.trackmixing.base.ui.widget.PivotableSeekbar
import com.smascaro.trackmixing.base.utils.ResourcesWrapper
import com.smascaro.trackmixing.base.utils.navigation.NavigationHelper
import com.smascaro.trackmixing.playback.model.TrackInstrument
import com.smascaro.trackmixing.playback.model.TrackVolumeBundle
import com.smascaro.trackmixing.playback.service.MixPlayerServiceChecker
import com.smascaro.trackmixing.playback.utils.state.PlaybackStateManager
import com.smascaro.trackmixing.playback.utils.state.SharedPreferencesFactory
import com.smascaro.trackmixing.player.model.TrackPlayerData
import javax.inject.Inject

@Suppress("ClassName")
class TrackPlayerViewMvcImpl_Activity @Inject constructor(
    private val serviceChecker: MixPlayerServiceChecker,
    private val resources: ResourcesWrapper,
    private val navigationHelper: NavigationHelper
) :
    BaseObservableViewMvc<TrackPlayerViewMvc.Listener>(),
    TrackPlayerViewMvc,
    SharedPreferences.OnSharedPreferenceChangeListener {

    //Views
    private lateinit var playPauseButton: ImageButton
    private lateinit var currentTimestampTextView: TextView
    private lateinit var totalLengthTextView: TextView
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
        playPauseButton = findViewById(R.id.btn_play_pause)
        currentTimestampTextView = findViewById(R.id.tv_track_player_current_timestamp)
        totalLengthTextView = findViewById(R.id.tv_track_player_length)
        songProgressSeekbar = findViewById(R.id.sb_track_player_timestamp)

        vocalsVolumeSeekbar = findViewById(R.id.sb_track_player_vocals)
        otherVolumeSeekbar = findViewById(R.id.sb_track_player_other)
        bassVolumeSeekbar = findViewById(R.id.sb_track_player_bass)
        drumsVolumeSeekbar = findViewById(R.id.sb_track_player_drums)

        playPauseButton.setOnClickListener { getListeners().forEach { it.onActionButtonClicked() } }
        setupSharedPreferences()
    }

    override fun initializeListeners() {
        super.initializeListeners()

        vocalsVolumeSeekbar.setOnSeekBarChangeListener(makeSeekbarChangeListenerFor(TrackInstrument.VOCALS))
        otherVolumeSeekbar.setOnSeekBarChangeListener(makeSeekbarChangeListenerFor(TrackInstrument.OTHER))
        bassVolumeSeekbar.setOnSeekBarChangeListener(makeSeekbarChangeListenerFor(TrackInstrument.BASS))
        drumsVolumeSeekbar.setOnSeekBarChangeListener(makeSeekbarChangeListenerFor(TrackInstrument.DRUMS))

        initializeProgressSeekBarListener()
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

    private fun makeSeekbarChangeListenerFor(trackInstrument: TrackInstrument): TrackMixerSeekBarChangeListener {
        return TrackMixerSeekBarChangeListener { progress ->
            getListeners().forEach { listener ->
                listener.onTrackVolumeChanged(trackInstrument, progress)
            }
        }
    }

    override fun bindTrackDuration(length: Seconds) {
        totalLengthTextView.text =
            transformSecondsToTimeRepresentation(length.value.toInt())
        songProgressSeekbar.max = length.value.toInt()
    }

    override fun bindVolumes(
        volumes: TrackVolumeBundle
    ) {
        vocalsVolumeSeekbar.progress = volumes.vocals
        otherVolumeSeekbar.progress = volumes.other
        bassVolumeSeekbar.progress = volumes.bass
        drumsVolumeSeekbar.progress = volumes.drums
    }

    override fun onBackPressed(): Boolean {
        navigationHelper.back()
        return true
    }

    private fun transformSecondsToTimeRepresentation(seconds: Int): String {
        return TimeHelper.fromSeconds(seconds.toLong()).toStringRepresentation()
    }

    private fun setupSharedPreferences() {
        sharedPreferences =
            SharedPreferencesFactory.getPlaybackSharedPreferencesFactory(
                getContext()!!
            )
        sharedPreferences.registerOnSharedPreferenceChangeListener(this)
    }

    override fun showPlayerBar(data: TrackPlayerData) {
        /* nothing to do */
    }

    override fun showPlayButton() {
        playPauseButton.setBackgroundResource(R.drawable.ic_play)
    }

    override fun showPauseButton() {
        playPauseButton.setBackgroundResource(R.drawable.ic_pause)
    }

    override fun onSharedPreferenceChanged(sharedPreferences: SharedPreferences?, key: String?) {
        if (key != null && key == PlaybackStateManager.SHARED_PREFERENCES_PLAYBACK_SONG_PLAYING || key == PlaybackStateManager.SHARED_PREFERENCES_PLAYBACK_IS_PLAYING) {
            getListeners().forEach {
                it.onPlayerStateChanged()
            }
        }
    }

    override fun updateTimestamp(newTimestamp: Int, totalLength: Int) {
        currentTimestampTextView.text =
            transformSecondsToTimeRepresentation(newTimestamp)
        songProgressSeekbar.progress = newTimestamp
    }
}