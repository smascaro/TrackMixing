package com.smascaro.trackmixing.base.ui.architecture.view

interface Observable<ListenerType> {
    fun registerListener(listener: ListenerType)
    fun unregisterListener(listener: ListenerType)
}