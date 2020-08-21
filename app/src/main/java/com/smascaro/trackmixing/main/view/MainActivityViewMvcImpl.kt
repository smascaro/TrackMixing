package com.smascaro.trackmixing.main.view

import android.widget.Toast
import com.smascaro.trackmixing.common.utils.PlaybackStateManager
import com.smascaro.trackmixing.common.view.architecture.BaseObservableViewMvc
import com.smascaro.trackmixing.player.business.downloadtrack.TrackDownloadService
import javax.inject.Inject
import kotlin.concurrent.thread

class MainActivityViewMvcImpl @Inject constructor(private val playbackStateManager: PlaybackStateManager) :
    MainActivityViewMvc,
    BaseObservableViewMvc<MainActivityViewMvc.Listener>() {

    override fun showMessage(text: String) {
        Toast.makeText(getContext(), text, Toast.LENGTH_SHORT).show()
    }

    override fun startProcessingRequest(url: String) {
        if (getContext() != null) {
            thread {
                TrackDownloadService.start(getContext()!!, url)
            }
        }
    }

}