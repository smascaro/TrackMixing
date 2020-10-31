package com.smascaro.trackmixing.playbackservice.controller

import com.smascaro.trackmixing.common.data.model.ForegroundNotification

open class ServiceCallbackHandler {
    protected var callbackStopService: () -> Unit = {}
    protected var callbackStartForeground: (ForegroundNotification) -> Unit = {}
    protected var callbackStopForeground: (Boolean) -> Unit = {}

    fun setStopServiceHandler(handler: () -> Unit) {
        callbackStopService = handler
    }

    fun setStartForegroundHandler(handler: (ForegroundNotification) -> Unit) {
        callbackStartForeground = handler
    }

    fun setStopForegroundHandler(handler: (Boolean) -> Unit) {
        callbackStopForeground = handler
    }
}