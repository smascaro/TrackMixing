package com.smascaro.trackmixing.main.components.bottomplayer.view

import android.view.GestureDetector
import android.view.MotionEvent
import android.view.View

class BottomPlayerOnTouchListener(private val gestureDetector: GestureDetector) :
    View.OnTouchListener {
    var lastXDown = -1f
    var lastYDown = -1f
    override fun onTouch(v: View?, event: MotionEvent?): Boolean {
        if (event?.action == MotionEvent.ACTION_DOWN) {
            lastXDown = event.x
            lastYDown = event.y
        }
        if (event?.action == MotionEvent.ACTION_UP) {
            val xDiff = lastXDown - event.x
            val yDiff = lastYDown - event.y
            if (xDiff <= 10 && yDiff <= 10) {
                return v?.performClick() ?: false
            }
        }
        gestureDetector.onTouchEvent(event)
        return true
    }
}