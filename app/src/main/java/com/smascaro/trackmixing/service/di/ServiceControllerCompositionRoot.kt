package com.smascaro.trackmixing.service.di

import android.content.Context
import com.smascaro.trackmixing.common.di.CompositionRoot
import com.smascaro.trackmixing.service.PlayingHelper
import com.smascaro.trackmixing.ui.notification.NotificationHelper

class ServiceControllerCompositionRoot(
    private val mCompositionRoot: CompositionRoot,
    private val mContext: Context
) {
    private fun getContext(): Context {
        return mContext
    }

    fun getPlayingHelper(): PlayingHelper {
        return PlayingHelper()
    }

    fun getNotificationHelper(): NotificationHelper {
        return NotificationHelper(getContext())
    }
}