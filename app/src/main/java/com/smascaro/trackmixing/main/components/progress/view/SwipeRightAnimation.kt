package com.smascaro.trackmixing.main.components.progress.view

import android.view.View
import android.view.animation.Animation
import android.view.animation.Transformation

class SwipeRightAnimation(private val view: View, private val rootView: View) : Animation() {
    private val initialX: Float = view.x
    private val deltaX: Float

    init {
        deltaX = rootView.right - initialX
    }

    override fun applyTransformation(interpolatedTime: Float, t: Transformation?) {
        view.x = (initialX + deltaX * interpolatedTime)
        view.alpha = 1 - interpolatedTime
        view.requestLayout()
    }

    override fun willChangeBounds(): Boolean {
        return true
    }
}