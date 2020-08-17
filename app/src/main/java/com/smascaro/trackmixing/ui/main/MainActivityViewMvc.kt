package com.smascaro.trackmixing.ui.main

import com.smascaro.trackmixing.ui.common.ObservableViewMvc

interface MainActivityViewMvc : ObservableViewMvc<MainActivityViewMvc.Listener> {
    interface Listener

    fun showMessage(text: String)
    fun checkPlaybackState()
}