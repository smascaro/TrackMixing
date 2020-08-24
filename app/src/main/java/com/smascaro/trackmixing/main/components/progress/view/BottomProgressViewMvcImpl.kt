package com.smascaro.trackmixing.main.components.progress.view

import android.view.LayoutInflater
import android.view.View
import android.widget.LinearLayout
import android.widget.TextSwitcher
import androidx.core.view.children
import com.smascaro.trackmixing.R
import com.smascaro.trackmixing.common.utils.ResourcesWrapper
import com.smascaro.trackmixing.common.view.architecture.BaseViewMvc
import timber.log.Timber
import javax.inject.Inject

class BottomProgressViewMvcImpl @Inject constructor(resources: ResourcesWrapper) : BaseViewMvc(),
    BottomProgressViewMvc {
    private lateinit var progressBarWrapper: LinearLayout
    private lateinit var progressBar: LinearLayout
    private var currentProgressValue: Int = -1
    private var currentProgressMessage: String = ""
    private lateinit var textSwitcherProgressValue: TextSwitcher
    private lateinit var textSwitcherProgressText: TextSwitcher

    private val progressBarVisibleHeight =
        resources.getDimension(R.dimen.download_progress_layout_visible_height)
    private val progressBarHiddenHeight =
        resources.getDimension(R.dimen.download_progress_layout_hidden_height)
    private val inAnimationDuration =
        resources.getLong(R.integer.animation_slide_in_bottom_duration)
    private val outAnimationDuration = resources.getLong(R.integer.animation_slide_out_top_duration)
    private var isProgressBarVisible: Boolean = false
    override fun bindRootView(rootView: View?) {
        super.bindRootView(rootView)
        initialize()
    }

    private fun initialize() {
        progressBarWrapper = findViewById(R.id.layout_progress_container)
        LayoutInflater.from(getContext())
            .inflate(R.layout.layout_download_progress, progressBarWrapper, false)
        progressBar = progressBarWrapper.findViewById(R.id.layout_progress_container)
        removeProgressBarWithoutAnimation()
        textSwitcherProgressValue = progressBarWrapper.findViewById(R.id.ts_progress_value)
        textSwitcherProgressText = progressBarWrapper.findViewById(R.id.ts_progress_message)

        textSwitcherProgressValue.setInAnimation(getContext(), R.anim.slide_in_bottom)
        textSwitcherProgressValue.setOutAnimation(getContext(), R.anim.slide_out_top)
        textSwitcherProgressText.setInAnimation(getContext(), R.anim.slide_in_bottom)
        textSwitcherProgressText.setOutAnimation(getContext(), R.anim.slide_out_top)
    }

    override fun startMarquee() {
        Timber.d("Starting marquee")
        textSwitcherProgressText.children.forEach {
            it.isSelected = true
        }
    }

    override fun showProgressBar() {
        if (!isProgressBarVisible) {
            displayProgressBar()
            isProgressBarVisible = true
        }
    }

    private fun displayProgressBar() {
        progressBar.visibility = View.VISIBLE
        val animation = ResizeAnimation(progressBar, progressBarVisibleHeight.toInt()).apply {
            duration = inAnimationDuration
        }
        progressBar.requestLayout()
        progressBar.startAnimation(animation)
    }

    override fun hideProgressBar() {
        if (isProgressBarVisible) {
            removeProgressBar()
            isProgressBarVisible = false
        }
    }

    private fun removeProgressBar() {
        val animation = ResizeAnimation(progressBar, progressBarHiddenHeight.toInt()).apply {
            duration = outAnimationDuration
        }
        progressBar.startAnimation(animation)
        progressBar.visibility = View.INVISIBLE
    }


    private fun setProgressBarVisibility(visibility: Int) {
        progressBar.visibility = visibility
    }

    private fun setProgressBarHeight(height: Int) {
        val layoutParams = progressBar.layoutParams
        layoutParams.height = height
        progressBar.layoutParams = layoutParams
    }

    override fun updateProgress(progress: Int, status: String) {
        if (currentProgressValue != progress) {
            textSwitcherProgressValue.setText("$progress%")
            currentProgressValue = progress
        }
        if (currentProgressMessage != status) {
            textSwitcherProgressText.setText(status)
            currentProgressMessage = status
        }
    }

    private fun removeProgressBarWithoutAnimation() {
        setProgressBarHeight(progressBarHiddenHeight.toInt())
        setProgressBarVisibility(View.INVISIBLE)
    }
}