package com.smascaro.trackmixing.ui.common

interface ObservableViewMvc<ListenerType> :
    ViewMvc {

    fun registerListener(listener: ListenerType)
    fun unregisterListener(listener: ListenerType)
}