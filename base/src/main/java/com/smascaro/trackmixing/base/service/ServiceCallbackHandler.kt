package com.smascaro.trackmixing.base.service


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