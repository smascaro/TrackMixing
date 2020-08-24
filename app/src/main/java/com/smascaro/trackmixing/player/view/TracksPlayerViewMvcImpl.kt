package com.smascaro.trackmixing.player.view

import android.view.View
import android.widget.SeekBar
import com.google.android.material.imageview.ShapeableImageView
import com.smascaro.trackmixing.R
import com.smascaro.trackmixing.common.view.architecture.BaseObservableViewMvc
import com.smascaro.trackmixing.playbackservice.model.TrackInstrument
import javax.inject.Inject

class TracksPlayerViewMvcImpl @Inject constructor() :
    BaseObservableViewMvc<TracksPlayerViewMvc.Listener>(),
    TracksPlayerViewMvc {

    private var mIsServiceStarted: Boolean = false

    private lateinit var vocalsVolumeSeekbar: SeekBar
    private lateinit var otherVolumeSeekbar: SeekBar
    private lateinit var bassVolumeSeekbar: SeekBar
    private lateinit var drumsVolumeSeekbar: SeekBar

    private lateinit var actionButtonImageView: ShapeableImageView

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
        volumesMap: Map<TrackInstrument, Int>
    ) {
        vocalsVolumeSeekbar.progress = volumesMap[TrackInstrument.VOCALS] ?: 100
        otherVolumeSeekbar.progress = volumesMap[TrackInstrument.OTHER] ?: 100
        bassVolumeSeekbar.progress = volumesMap[TrackInstrument.BASS] ?: 100
        drumsVolumeSeekbar.progress = volumesMap[TrackInstrument.DRUMS] ?: 100
    }

}