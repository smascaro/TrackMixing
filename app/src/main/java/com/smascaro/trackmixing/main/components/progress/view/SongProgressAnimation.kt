package com.smascaro.trackmixing.main.components.progress.view

import android.view.View
import android.view.animation.Animation
import android.view.animation.Transformation

class SongProgressAnimation(private val view: View, private val newWidth: Int) : Animation() {
    private val initialWidth: Int = view.layoutParams.width
    private val deltaWidth: Int

    init {
        deltaWidth = newWidth - initialWidth
    }

    override fun applyTransformation(interpolatedTime: Float, t: Transformation?) {
        view.layoutParams.width = (initialWidth + deltaWidth * interpolatedTime).toInt()
        view.requestLayout()
    }

    override fun willChangeBounds(): Boolean {
        return true
    }
}