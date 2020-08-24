package com.smascaro.trackmixing.main.components.progress.controller

import com.smascaro.trackmixing.R
import com.smascaro.trackmixing.common.controller.BaseController
import com.smascaro.trackmixing.common.utils.ResourcesWrapper
import com.smascaro.trackmixing.main.components.progress.model.UiProgressEvent
import com.smascaro.trackmixing.main.components.progress.view.BottomProgressViewMvc
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import timber.log.Timber
import javax.inject.Inject

class BottomProgressController @Inject constructor(resources: ResourcesWrapper) :
    BaseController<BottomProgressViewMvc>() {

    private val delayBeforeHidingMillis =
        resources.getLong(R.integer.download_progress_delay_before_hiding_millis)

    fun onCreate() {
        ensureViewMvcBound()
        viewMvc.startMarquee()
    }

    fun onStart() {
        Timber.d("BottomProgressController - OnStart - Register to event bus")
        EventBus.getDefault().register(this)
    }

    fun onStop() {
        Timber.d("BottomProgressController - OnStop - Unregister from event bus")
        EventBus.getDefault().unregister(this)
    }

    private fun handleProgressUpdate(event: UiProgressEvent.ProgressUpdate) {
        Timber.d("BottomProgressController - Received ProgressUpdate event")
        viewMvc.showProgressBar()
        viewMvc.updateProgress(event.progress, event.status)
    }


    private fun handleErrorOccurredEvent(event: UiProgressEvent.ErrorOccurred) {
        Timber.d("BottomProgressController - Received ErrorOccurred event")
        delayedHideProgress(0, event.message)
    }

    private fun handleProgressFinishedEvent() {
        Timber.d("BottomProgressController - Received ProgressFinished event")
        delayedHideProgress(100, "Finished processing")
    }

    private fun delayedHideProgress(progress: Int, message: String) {
        viewMvc.updateProgress(progress, message)
        CoroutineScope(Dispatchers.Main).launch {
            delay(delayBeforeHidingMillis)
            viewMvc.hideProgressBar()
        }
    }

    @Subscribe(threadMode = ThreadMode.BACKGROUND)
    fun onMessageEvent(event: UiProgressEvent.ProgressUpdate) =
        CoroutineScope(Dispatchers.Main).launch { handleProgressUpdate(event) }

    @Subscribe(threadMode = ThreadMode.BACKGROUND)
    fun onMessageEvent(event: UiProgressEvent.ProgressFinished) =
        CoroutineScope(Dispatchers.Main).launch { handleProgressFinishedEvent() }

    @Subscribe(threadMode = ThreadMode.BACKGROUND)
    fun onMessageEvent(event: UiProgressEvent.ErrorOccurred) =
        CoroutineScope(Dispatchers.Main).launch { handleErrorOccurredEvent(event) }
}

