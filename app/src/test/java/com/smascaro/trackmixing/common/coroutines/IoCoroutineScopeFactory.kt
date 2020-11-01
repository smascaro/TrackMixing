package com.smascaro.trackmixing.common.coroutines

import com.smascaro.trackmixing.common.di.coroutines.IoCoroutineScope
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Job

class IoCoroutineScopeFactory {
    companion object {
        fun create(dispatcher: CoroutineDispatcher): IoCoroutineScope {
            return object : IoCoroutineScope {
                override val coroutineContext = Job() + dispatcher
            }
        }
    }
}