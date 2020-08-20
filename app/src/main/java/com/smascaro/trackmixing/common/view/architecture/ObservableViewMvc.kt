package com.smascaro.trackmixing.common.view.architecture

interface ObservableViewMvc<ListenerType> :
    ViewMvc {

    fun registerListener(listener: ListenerType)
    fun unregisterListener(listener: ListenerType)
}