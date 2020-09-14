package com.smascaro.trackmixing.common.view.architecture

interface Observable<ListenerType> {
    fun registerListener(listener: ListenerType)
    fun unregisterListener(listener: ListenerType)
}