package com.smascaro.trackmixing.main.components.player.view

import android.widget.SeekBar

class ProgressSeekBarChangeListener : SeekBar.OnSeekBarChangeListener {
    private var progressChanged: (SeekBar?, Int, Boolean) -> Unit = { _, _, _ -> }
    private var startTrackingTouch: (SeekBar?) -> Unit = { }
    private var stopTrackingTouch: (SeekBar?) -> Unit = { }
    fun setOnProgressChanged(onProgressChangeAction: (SeekBar?, Int, Boolean) -> Unit) {
        progressChanged = onProgressChangeAction
    }

    fun setOnStartTrackingTouch(onStartTrackingTouch: (SeekBar?) -> Unit) {
        startTrackingTouch = onStartTrackingTouch
    }

    fun setOnStopTrackingTouch(onStopTrackingTouch: (SeekBar?) -> Unit) {
        stopTrackingTouch = onStopTrackingTouch
    }

    override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
        progressChanged(seekBar, progress, fromUser)
    }

    override fun onStartTrackingTouch(seekBar: SeekBar?) {
        startTrackingTouch(seekBar)
    }

    override fun onStopTrackingTouch(seekBar: SeekBar?) {
        stopTrackingTouch(seekBar)
    }
}