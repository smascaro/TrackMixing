package com.smascaro.trackmixing.base.ui.architecture.view

import java.util.*

abstract class BaseObservableViewMvc<ListenerType> : BaseViewMvc(),
    ObservableViewMvc<ListenerType> {
    private var mListeners: MutableSet<ListenerType> = hashSetOf()
    override fun registerListener(listener: ListenerType) {
        mListeners.add(listener)
    }

    override fun unregisterListener(listener: ListenerType) {
        mListeners.remove(listener)
    }

    protected fun getListeners(): Set<ListenerType> {
        return Collections.unmodifiableSet(mListeners)
    }
}