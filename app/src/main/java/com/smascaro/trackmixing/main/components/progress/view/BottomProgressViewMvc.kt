package com.smascaro.trackmixing.main.components.progress.view

import com.smascaro.trackmixing.base.ui.architecture.view.ViewMvc

interface BottomProgressViewMvc : ViewMvc {
    fun startMarquee()
    fun showProgressBar()
    fun hideProgressBar()
    fun updateProgress(progress: Int, status: String)
}