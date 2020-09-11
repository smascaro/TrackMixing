package com.smascaro.trackmixing.player.view

import android.view.View
import android.widget.SeekBar
import androidx.constraintlayout.widget.ConstraintLayout
import com.google.android.material.imageview.ShapeableImageView
import com.google.android.material.textview.MaterialTextView
import com.smascaro.trackmixing.R
import com.smascaro.trackmixing.common.utils.TimeHelper
import com.smascaro.trackmixing.common.utils.TrackVolumeBundle
import com.smascaro.trackmixing.common.view.architecture.BaseObservableViewMvc
import com.smascaro.trackmixing.playbackservice.model.TrackInstrument
import com.smascaro.trackmixing.player.view.widget.VerticalSeekbar
import javax.inject.Inject

class TracksPlayerViewMvcImpl @Inject constructor() :
    BaseObservableViewMvc<TracksPlayerViewMvc.Listener>(),
    TracksPlayerViewMvc {

    private var mIsServiceStarted: Boolean = false

    private lateinit var vocalsVolumeSeekbar: VerticalSeekbar
    private lateinit var otherVolumeSeekbar: VerticalSeekbar
    private lateinit var bassVolumeSeekbar: VerticalSeekbar
    private lateinit var drumsVolumeSeekbar: VerticalSeekbar

    private lateinit var actionButtonImageView: ShapeableImageView

    private lateinit var currentTimestampTextView: MaterialTextView
    private lateinit var totalLengthTextView: MaterialTextView
    private lateinit var songProgressSeekbar: SeekBar
    private lateinit var containerLayout: ConstraintLayout

    override fun bindRootView(rootView: View?) {
        super.bindRootView(rootView)
        initialize()
    }

    private fun initialize() {
        vocalsVolumeSeekbar = findViewById(R.id.sb_track_player_vocals)
        otherVolumeSeekbar = findViewById(R.id.sb_track_player_other)
        bassVolumeSeekbar = findViewById(R.id.sb_track_player_bass)
        drumsVolumeSeekbar = findViewById(R.id.sb_track_player_drums)

        actionButtonImageView = findViewById(R.id.iv_track_player_action)

        currentTimestampTextView = findViewById(R.id.tv_track_player_current_timestamp)
        totalLengthTextView = findViewById(R.id.tv_track_player_length)
        songProgressSeekbar = findViewById(R.id.sb_track_player_timestamp)
        containerLayout = findViewById(R.id.layout_track_player_container)
        initializeListeners()
    }

    private fun initializeListeners() {
        vocalsVolumeSeekbar.setOnSeekBarChangeListener(makeSeekbarChangeListenerFor(TrackInstrument.VOCALS))
        otherVolumeSeekbar.setOnSeekBarChangeListener(makeSeekbarChangeListenerFor(TrackInstrument.OTHER))
        bassVolumeSeekbar.setOnSeekBarChangeListener(makeSeekbarChangeListenerFor(TrackInstrument.BASS))
        drumsVolumeSeekbar.setOnSeekBarChangeListener(makeSeekbarChangeListenerFor(TrackInstrument.DRUMS))

        actionButtonImageView.setOnClickListener {
            getListeners().forEach {
                it.onActionButtonClicked()
            }
        }

        val progressChangeListener = ProgressSeekBarChangeListener()
        progressChangeListener.setOnProgressChanged { _, progress, _ ->
            currentTimestampTextView.text = transformSecondsToTimeRepresentation(progress)
        }

        progressChangeListener.setOnStopTrackingTouch { seekBar ->
            if (seekBar != null) {
                getListeners().forEach {
                    it.onSeekRequestEvent(seekBar.progress)
                }
            }
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

    override fun isServiceStarted(): Boolean {
        return mIsServiceStarted
    }

    override fun setCurrentVolume(trackInstrument: TrackInstrument, volume: Int) {
        when (trackInstrument) {
            TrackInstrument.VOCALS -> vocalsVolumeSeekbar.progress = volume
            TrackInstrument.OTHER -> otherVolumeSeekbar.progress = volume
            TrackInstrument.BASS -> bassVolumeSeekbar.progress = volume
            TrackInstrument.DRUMS -> drumsVolumeSeekbar.progress = volume
        }
    }

    override fun showPlayButton() {
        actionButtonImageView.setBackgroundResource(R.drawable.play_button)
    }

    override fun showPauseButton() {
        actionButtonImageView.setBackgroundResource(R.drawable.pause_button)
    }

    override fun onDestroy() {
        //no action
    }

    override fun bindVolumes(
        volumes: TrackVolumeBundle
    ) {
        vocalsVolumeSeekbar.progress = volumes.vocals
        otherVolumeSeekbar.progress = volumes.other
        bassVolumeSeekbar.progress = volumes.bass
        drumsVolumeSeekbar.progress = volumes.drums
    }

    override fun bindTrackDuration(lengthSeconds: Int) {
        totalLengthTextView.text =
            transformSecondsToTimeRepresentation(lengthSeconds)
        songProgressSeekbar.max = lengthSeconds
    }

    override fun bindBackgroundColor(color: Int) {
        containerLayout.setBackgroundColor(color)
    }

    override fun updateTimestamp(timestamp: Int) {
        currentTimestampTextView.text =
            transformSecondsToTimeRepresentation(timestamp)
        songProgressSeekbar.progress = timestamp
    }

    private fun transformSecondsToTimeRepresentation(seconds: Int): String {
        return TimeHelper.fromSeconds(seconds.toLong()).toStringRepresentation()
    }
}