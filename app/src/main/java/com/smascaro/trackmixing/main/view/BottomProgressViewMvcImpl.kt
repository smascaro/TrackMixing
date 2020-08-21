package com.smascaro.trackmixing.main.view

import android.view.View
import android.widget.LinearLayout
import com.google.android.material.textview.MaterialTextView
import com.smascaro.trackmixing.R
import com.smascaro.trackmixing.common.view.architecture.BaseViewMvc
import timber.log.Timber
import javax.inject.Inject

class BottomProgressViewMvcImpl @Inject constructor() : BaseViewMvc(), BottomProgressViewMvc {
    private lateinit var progressBar: LinearLayout
    private lateinit var textViewProgressValue: MaterialTextView
    private lateinit var textViewProgressText: MaterialTextView
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
}