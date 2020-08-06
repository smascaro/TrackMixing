package com.smascaro.trackmixing.ui.animation

import android.view.View
import android.view.ViewGroup
import android.view.animation.Animation
import android.view.animation.Transformation

class Animations {
    companion object {
        fun expandDetails(view: View) {
            val animation = buildExpandAnimation(view)
            view.startAnimation(animation)
        }

        fun collapseDetails(view: View) {
            val animation = buildCollapseAnimation(view)
            view.startAnimation(animation)
        }

        private fun buildExpandAnimation(view: View): Animation {
            view.measure(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
            val actualHeight = view.measuredHeight
            view.layoutParams.height = 0
            view.visibility = View.VISIBLE

            val animation = ExpandAnimation().apply {
                bindView(view)
                bindActualHeight(actualHeight)
                duration = 300
            }
//            view.startAnimation(animation)
            return animation
        }

        private fun buildCollapseAnimation(view: View): Animation {
            val actualHeight = view.measuredHeight

            val animation = CollapseAnimation().apply {
                bindView(view)
                bindActualHeight(actualHeight)
                duration = 300
            }
//            view.startAnimation(animation)
            return animation
        }

    }

    private class ExpandAnimation : Animation() {
        private lateinit var mView: View
        private var mActualHeight: Int = 0
        fun bindView(view: View) {
            mView = view
        }

        fun bindActualHeight(actualHeight: Int) {
            mActualHeight = actualHeight
        }

        override fun applyTransformation(interpolatedTime: Float, t: Transformation?) {
            mView.layoutParams.height = if (interpolatedTime == 1f) {
                ViewGroup.LayoutParams.WRAP_CONTENT
            } else {
                mActualHeight * interpolatedTime.toInt()
            }
            mView.requestLayout()
        }
    }

    private class CollapseAnimation : Animation() {
        private lateinit var mView: View
        private var mActualHeight: Int = 0
        fun bindView(view: View) {
            mView = view
        }

        fun bindActualHeight(actualHeight: Int) {
            mActualHeight = actualHeight
        }

        override fun applyTransformation(interpolatedTime: Float, t: Transformation?) {
            if (interpolatedTime == 1f) {
                mView.visibility = View.GONE
            } else {
                mView.layoutParams.height =
                    mActualHeight - (mActualHeight * interpolatedTime.toInt())
                mView.requestLayout()
            }
        }
    }
}