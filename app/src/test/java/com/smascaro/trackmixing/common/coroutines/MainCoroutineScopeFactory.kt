package com.smascaro.trackmixing.common.coroutines

import com.smascaro.trackmixing.common.di.coroutines.MainCoroutineScope
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Job

class MainCoroutineScopeFactory {
    companion object {
        fun create(dispatcher: CoroutineDispatcher): MainCoroutineScope {
            return object : MainCoroutineScope {
                override val coroutineContext = Job() + dispatcher
            }
        }
    }
}