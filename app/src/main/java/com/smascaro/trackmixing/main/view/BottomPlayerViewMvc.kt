package com.smascaro.trackmixing.main.view

import com.smascaro.trackmixing.common.data.model.Track
import com.smascaro.trackmixing.common.view.architecture.ObservableViewMvc
import com.smascaro.trackmixing.main.model.BottomPlayerData

interface BottomPlayerViewMvc :
    ObservableViewMvc<BottomPlayerViewMvc.Listener> {
    interface Listener {
        fun onLayoutClick()
        fun onActionButtonClicked()
        fun onPlayerStateChanged()
    }

    fun showPlayerBar(data: BottomPlayerData)
    fun hidePlayerBar()
    fun showPlayButton()
    fun showPauseButton()
    fun navigateToPlayer(track: Track)
}
