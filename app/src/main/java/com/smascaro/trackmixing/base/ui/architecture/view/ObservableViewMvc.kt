package com.smascaro.trackmixing.base.ui.architecture.view

interface ObservableViewMvc<ListenerType> :
    ViewMvc {
    fun registerListener(listener: ListenerType)
    fun unregisterListener(listener: ListenerType)
}