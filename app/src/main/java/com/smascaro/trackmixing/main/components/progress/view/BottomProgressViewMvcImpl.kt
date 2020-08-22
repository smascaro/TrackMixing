package com.smascaro.trackmixing.main.components.progress.view

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
        progressBar = findViewById(R.id.layout_progress_container)
        textSwitcherProgressValue = findViewById(R.id.ts_progress_value)
        textSwitcherProgressText = findViewById(R.id.ts_progress_message)

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
            setProgressBarVisibility(View.VISIBLE)
            setProgressBarHeight(progressBarHeight.toInt())
            isProgressBarVisible = true
        }
    }

    override fun hideProgressBar() {
        if (isProgressBarVisible) {
            setProgressBarHeight(0)
            setProgressBarVisibility(View.GONE)
            isProgressBarVisible = false
        }
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
}