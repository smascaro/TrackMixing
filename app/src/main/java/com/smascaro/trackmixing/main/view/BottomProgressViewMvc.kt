package com.smascaro.trackmixing.main.view

import com.smascaro.trackmixing.common.view.architecture.ViewMvc

interface BottomProgressViewMvc : ViewMvc {
    fun startMarquee()
    fun showProgressBar()
    fun hideProgressBar()
    fun updateProgress(progress: Int, status: String)
}