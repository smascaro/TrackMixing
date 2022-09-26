package com.smascaro.trackmixing.player.view

import android.widget.SeekBar

class TrackMixerSeekBarChangeListener(
    private val onProgressChangeAction: (Int) -> Unit
) : SeekBar.OnSeekBarChangeListener {
    companion object {
        fun create(onProgressChangeAction: (Int) -> Unit): TrackMixerSeekBarChangeListener {
            return TrackMixerSeekBarChangeListener(onProgressChangeAction)
        }
    }

    override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
        onProgressChangeAction(progress)
    }

    override fun onStartTrackingTouch(seekBar: SeekBar?) {
        // Nothing to do
    }

    override fun onStopTrackingTouch(seekBar: SeekBar?) {
        // Nothing to do
    }
}