package com.smascaro.trackmixing.main.view

import android.view.View
import android.widget.LinearLayout
import com.google.android.material.textview.MaterialTextView
import com.smascaro.trackmixing.R
import com.smascaro.trackmixing.common.utils.UiUtils
import com.smascaro.trackmixing.common.view.architecture.BaseViewMvc
import timber.log.Timber
import javax.inject.Inject

class BottomProgressViewMvcImpl @Inject constructor(private val uiUtils: UiUtils) : BaseViewMvc(),
    BottomProgressViewMvc {
    private lateinit var progressBar: LinearLayout
    private lateinit var textViewProgressValue: MaterialTextView
    private lateinit var textViewProgressText: MaterialTextView

    private val progressBarHeight = uiUtils.DpToPixels(26f)

    private var isProgressBarVisible: Boolean = false
    override fun bindRootView(rootView: View?) {
        super.bindRootView(rootView)
        initialize()
    }

    private fun initialize() {
        progressBar = findViewById(R.id.layout_progress_container)
        textViewProgressValue = findViewById(R.id.tv_progress_value)
        textViewProgressText = findViewById(R.id.tv_progress_message)
    }

    override fun startMarquee() {
        Timber.d("Starting marquee")
        textViewProgressText.isSelected = true
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
        textViewProgressValue.text = "$progress%"
        if (textViewProgressText.text != status) {
            textViewProgressText.text = status
        }
    }
}