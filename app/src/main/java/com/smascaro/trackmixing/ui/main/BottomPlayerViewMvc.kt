package com.smascaro.trackmixing.ui.main

import com.smascaro.trackmixing.tracks.Track
import com.smascaro.trackmixing.ui.common.ObservableViewMvc

interface BottomPlayerViewMvc : ObservableViewMvc<BottomPlayerViewMvc.Listener> {
    interface Listener {
        fun onLayoutClick()
        fun onActionButtonClicked()
        fun onPlayerStateChanged()
    }

    fun showPlayerBar(data: BottomPlayerData)
    fun hidePlayerBar()
    fun navigateToPlayer(track: Track)
}
