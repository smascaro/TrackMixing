package com.smascaro.trackmixing.main.components.bottomplayer.view

import com.smascaro.trackmixing.base.ui.architecture.view.ObservableViewMvc

interface BottomPlayerViewMvc : ObservableViewMvc<BottomPlayerViewMvc.Listener> {
    interface Listener {
        fun onBottomPlayerClick()
        fun onActionButtonClicked()
        fun onPlayerStateChanged()
        fun onServiceRunningCheck(running: Boolean)
        fun onPlayerSwipedOut()
    }

    fun onCreate()
    fun showPlayerBar(data: com.smascaro.trackmixing.player.model.TrackPlayerData)
    fun hidePlayerBar()
    fun showPlayButton()
    fun showPauseButton()
}