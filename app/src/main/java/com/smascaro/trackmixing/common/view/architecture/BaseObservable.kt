package com.smascaro.trackmixing.common.view.architecture

open class BaseObservable<LISTENER_CLASS> {
    private val mListeners: MutableSet<LISTENER_CLASS> = HashSet()

    fun registerListener(listener: LISTENER_CLASS) {
        mListeners.add(listener)
    }

    fun unregisterListener(listener: LISTENER_CLASS) {
        mListeners.remove(listener)
    }

    fun getListeners(): Set<LISTENER_CLASS> {
        return mListeners.toSet()
    }
}