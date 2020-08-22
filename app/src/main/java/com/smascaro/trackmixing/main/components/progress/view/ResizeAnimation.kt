package com.smascaro.trackmixing.main.components.progress.view

import android.view.View
import android.view.animation.Animation
import android.view.animation.Transformation

class ResizeAnimation(private val view: View, private val newHeight: Int) : Animation() {
    private val initialHeight: Int = view.layoutParams.height
    private val deltaHeight: Int

    init {
        deltaHeight = newHeight - initialHeight
    }

    override fun applyTransformation(interpolatedTime: Float, t: Transformation?) {
        view.layoutParams.height = (initialHeight + deltaHeight * interpolatedTime).toInt()
        view.requestLayout()
    }

    override fun willChangeBounds(): Boolean {
        return true
    }
}