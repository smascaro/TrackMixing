package com.smascaro.trackmixing.player.view

import android.content.SharedPreferences
import android.widget.ImageView
import android.widget.SeekBar
import com.google.android.material.textview.MaterialTextView
import com.smascaro.trackmixing.base.time.Seconds
import com.smascaro.trackmixing.base.time.TimeHelper
import com.smascaro.trackmixing.base.ui.architecture.view.BaseObservableViewMvc
import com.smascaro.trackmixing.base.ui.widget.PivotableSeekbar
import com.smascaro.trackmixing.playback.model.TrackInstrument
import com.smascaro.trackmixing.playback.model.TrackVolumeBundle
import com.smascaro.trackmixing.playback.utils.state.PlaybackStateManager
import com.smascaro.trackmixing.playback.utils.state.SharedPreferencesFactory
import com.smascaro.trackmixing.player.R
import javax.inject.Inject

class TrackPlayerViewMvcImpl @Inject constructor() :
    BaseObservableViewMvc<TrackPlayerViewMvc.Listener>(),
    TrackPlayerViewMvc,
    SharedPreferences.OnSharedPreferenceChangeListener {

    //Views
    private lateinit var playPauseButton: ImageView
    private lateinit var currentTimestampTextView: MaterialTextView
    private lateinit var totalLengthTextView: MaterialTextView
    private lateinit var songProgressSeekbar: SeekBar
    private lateinit var trackTitleTextView: MaterialTextView

    //SeekBars
    private lateinit var vocalsVolumeSeekbar: PivotableSeekbar
    private lateinit var otherVolumeSeekbar: PivotableSeekbar
    private lateinit var bassVolumeSeekbar: PivotableSeekbar
    private lateinit var drumsVolumeSeekbar: PivotableSeekbar

    //Private fields
    private lateinit var sharedPreferences: SharedPreferences
    private var blockTimestampUpdates: Boolean = false

    override fun initialize() {
        super.initialize()
        currentTimestampTextView = findViewById(R.id.tv_track_player_current_timestamp)
        totalLengthTextView = findViewById(R.id.tv_track_player_length)
        songProgressSeekbar = findViewById(R.id.sb_track_player_timestamp)
        playPauseButton = findViewById(R.id.iv_player_play_pause_button)
        trackTitleTextView = findViewById(R.id.tv_track_player_title)

        vocalsVolumeSeekbar = findViewById(R.id.sb_track_player_vocals)
        otherVolumeSeekbar = findViewById(R.id.sb_track_player_other)
        bassVolumeSeekbar = findViewById(R.id.sb_track_player_bass)
        drumsVolumeSeekbar = findViewById(R.id.sb_track_player_drums)

        setupSharedPreferences()
    }

    override fun initializeListeners() {
        super.initializeListeners()

        vocalsVolumeSeekbar.setOnSeekBarChangeListener(makeSeekbarChangeListenerFor(TrackInstrument.VOCALS))
        otherVolumeSeekbar.setOnSeekBarChangeListener(makeSeekbarChangeListenerFor(TrackInstrument.OTHER))
        bassVolumeSeekbar.setOnSeekBarChangeListener(makeSeekbarChangeListenerFor(TrackInstrument.BASS))
        drumsVolumeSeekbar.setOnSeekBarChangeListener(makeSeekbarChangeListenerFor(TrackInstrument.DRUMS))

        initializeProgressSeekBarListener()
        initializeActionButtonListener()
    }

    private fun initializeActionButtonListener() {
        playPauseButton.setOnClickListener {
            getListeners().forEach { it.onActionButtonClicked() }
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

    override fun bindTrackTitle(title: String) {
        trackTitleTextView.text = title
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

    override fun showPlayButton() {
        playPauseButton.setImageResource(R.drawable.ic_play)
    }

    override fun showPauseButton() {
        playPauseButton.setImageResource(R.drawable.ic_pause)
    }

    override fun onSharedPreferenceChanged(sharedPreferences: SharedPreferences?, key: String?) {
        if (key != null && key == PlaybackStateManager.SHARED_PREFERENCES_PLAYBACK_SONG_PLAYING || key == PlaybackStateManager.SHARED_PREFERENCES_PLAYBACK_IS_PLAYING) {
            getListeners().forEach {
                it.onPlayerStateChanged()
            }
        }
    }

    override fun updateTimestamp(newTimestamp: Int, totalLength: Int) {
        if (!blockTimestampUpdates) {
            currentTimestampTextView.text =
                transformSecondsToTimeRepresentation(newTimestamp)
            songProgressSeekbar.progress = newTimestamp
        }
    }
}