package com.smascaro.trackmixing.common.testdoubles

import org.greenrobot.eventbus.EventBus
import timber.log.Timber

class EventBusTd : EventBus() {
    var isListenerRegistered = false
    var registeredListener: Any? = null
    var isListenerUnregistered = false
    var unregisteredListener: Any? = null
    override fun register(subscriber: Any?) {
        isListenerRegistered = true
        registeredListener = subscriber
    }

    override fun unregister(subscriber: Any?) {
        isListenerUnregistered = true
        unregisteredListener = subscriber
    }

    override fun post(event: Any?) {
        Timber.d("TD Post: $event")
    }
}