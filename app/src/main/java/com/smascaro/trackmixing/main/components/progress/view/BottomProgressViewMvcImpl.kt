package com.smascaro.trackmixing.main.components.progress.view

import android.view.LayoutInflater
import android.view.View
import android.widget.LinearLayout
import android.widget.TextSwitcher
import androidx.core.view.children
import com.smascaro.trackmixing.R
import com.smascaro.trackmixing.common.utils.UiUtils
import com.smascaro.trackmixing.common.view.architecture.BaseViewMvc
import timber.log.Timber
import javax.inject.Inject

class BottomProgressViewMvcImpl @Inject constructor(private val uiUtils: UiUtils) : BaseViewMvc(),
    BottomProgressViewMvc {
    private lateinit var progressBarWrapper: LinearLayout
    private lateinit var progressBar: LinearLayout
    private var currentProgressValue: Int = -1
    private var currentProgressMessage: String = ""
    private lateinit var textSwitcherProgressValue: TextSwitcher
    private lateinit var textSwitcherProgressText: TextSwitcher

    private val progressBarHeight = uiUtils.DpToPixels(26f)

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
        val animation = ResizeAnimation(progressBar, progressBarHeight.toInt()).apply {
            duration = 200
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
        val animation = ResizeAnimation(progressBar, 0).apply {
            duration = 200
        }
        progressBar.startAnimation(animation)
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

    override fun onCreate() {
        val lp = progressBar.layoutParams
        lp.height = 0
        progressBar.layoutParams = lp
        progressBar.visibility = View.VISIBLE
        progressBar.requestLayout()
    }
}