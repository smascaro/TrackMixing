package com.smascaro.trackmixing.utilities

import android.os.Handler
import android.os.Looper
import androidx.annotation.MainThread
import androidx.fragment.app.Fragment
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import kotlin.properties.ReadWriteProperty
import kotlin.reflect.KProperty

class NullifyOnDestroyValueProperty<T : Any>(
    fragment: Fragment
) : ReadWriteProperty<Fragment, T> {
    private var _value: T? = null
    private val lifecycleObserver = BindingLifecycleObserver()

    init {
        fragment.lifecycle.addObserver(lifecycleObserver)
    }

    @MainThread
    override fun getValue(thisRef: Fragment, property: KProperty<*>): T {
        return _value!!
    }

    override fun setValue(thisRef: Fragment, property: KProperty<*>, value: T) {
        this._value = value
    }

    private inner class BindingLifecycleObserver : DefaultLifecycleObserver {
        private val handler = Handler(Looper.getMainLooper())

        @MainThread
        override fun onDestroy(owner: LifecycleOwner) {
            super.onDestroy(owner)
            owner.lifecycle.removeObserver(this)
            // Fragment.viewLifecycleOwner call LifecycleObserver.onDestroy() before Fragment.onDestroyView().
            // That's why we need to postpone reset of the viewBinding
            handler.post {
                _value = null
            }
        }
    }
}

fun <T : Any> Fragment.nullifyOnDestroy(): NullifyOnDestroyValueProperty<T> {
    return NullifyOnDestroyValueProperty(this)
}
