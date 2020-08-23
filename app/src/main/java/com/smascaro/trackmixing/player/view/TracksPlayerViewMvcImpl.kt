package com.smascaro.trackmixing.player.view

import android.content.Intent
import android.view.View
import android.widget.SeekBar
import com.smascaro.trackmixing.R
import com.smascaro.trackmixing.common.view.architecture.BaseObservableViewMvc
import com.smascaro.trackmixing.playbackservice.model.TrackInstrument
import javax.inject.Inject

class TracksPlayerViewMvcImpl @Inject constructor(
) :
    BaseObservableViewMvc<TracksPlayerViewMvc.Listener>(),
    TracksPlayerViewMvc {

    private var mServiceIntent: Intent? = null
    private var mIsServiceStarted: Boolean = false

    private lateinit var vocalsVolumeSeekbar: SeekBar
    private lateinit var otherVolumeSeekbar: SeekBar
    private lateinit var bassVolumeSeekbar: SeekBar
    private lateinit var drumsVolumeSeekbar: SeekBar
    override fun bindRootView(rootView: View?) {
        super.bindRootView(rootView)
        initialize()
    }

    private fun initialize() {
        vocalsVolumeSeekbar = findViewById(R.id.sb_track_player_vocals)
        otherVolumeSeekbar = findViewById(R.id.sb_track_player_other)
        bassVolumeSeekbar = findViewById(R.id.sb_track_player_bass)
        drumsVolumeSeekbar = findViewById(R.id.sb_track_player_drums)

        initializeListeners()
    }

    private fun initializeListeners() {
        vocalsVolumeSeekbar.setOnSeekBarChangeListener(makeSeekbarChangeListenerFor(TrackInstrument.VOCALS))
        otherVolumeSeekbar.setOnSeekBarChangeListener(makeSeekbarChangeListenerFor(TrackInstrument.OTHER))
        bassVolumeSeekbar.setOnSeekBarChangeListener(makeSeekbarChangeListenerFor(TrackInstrument.BASS))
        drumsVolumeSeekbar.setOnSeekBarChangeListener(makeSeekbarChangeListenerFor(TrackInstrument.DRUMS))
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

    override fun onDestroy() {
        //no action
    }

}