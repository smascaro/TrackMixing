package com.smascaro.trackmixing.playbackservice.controller

import com.smascaro.trackmixing.common.data.model.ForegroundNotification

open class ServiceCallbackHandler {
    protected var handleStopService: () -> Unit = {}
    protected var handleStartForeground: (ForegroundNotification) -> Unit = {}
    protected var handleStopForeground: (Boolean) -> Unit = {}
    protected var handleError: (Throwable) -> Unit = {}

    fun setStopServiceHandler(handler: () -> Unit) {
        handleStopService = handler
    }

    fun setStartForegroundHandler(handler: (ForegroundNotification) -> Unit) {
        handleStartForeground = handler
    }

    fun setStopForegroundHandler(handler: (Boolean) -> Unit) {
        handleStopForeground = handler
    }

    fun setErrorHandler(handler: (Throwable) -> Unit) {
        handleError = handler
    }
}