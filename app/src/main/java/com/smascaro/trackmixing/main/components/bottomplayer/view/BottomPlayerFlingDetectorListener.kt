package com.smascaro.trackmixing.main.components.bottomplayer.view

import android.view.GestureDetector
import android.view.MotionEvent
import kotlin.math.abs

class BottomPlayerFlingDetectorListener : GestureDetector.SimpleOnGestureListener() {
    enum class FlingMode {
        LEFT_TO_RIGHT, BOTTOM_TO_TOP, NONE
    }

    var flingAction: (FlingMode) -> Unit? = {}
    fun setOnFlingAction(action: (FlingMode) -> Unit) {
        flingAction = action
    }

    override fun onFling(
        e1: MotionEvent?,
        e2: MotionEvent?,
        velocityX: Float,
        velocityY: Float
    ): Boolean {
        val fling =
            if (e1 != null && e2 != null) {
                if (e2.x - e1.x > 120 && abs(velocityX) > 200) {
                    FlingMode.LEFT_TO_RIGHT
                } else if (e1.y - e2.y > 120 && abs(velocityX) > 200) {
                    FlingMode.BOTTOM_TO_TOP
                } else {
                    FlingMode.NONE
                }
            } else {
                FlingMode.NONE
            }
        flingAction(fling)
        return fling != FlingMode.NONE
    }
}