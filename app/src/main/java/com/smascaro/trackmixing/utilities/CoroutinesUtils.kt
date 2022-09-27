package com.smascaro.trackmixing.utilities

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.CoroutineStart
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

var dispatcherMain: CoroutineDispatcher = Dispatchers.Main
var dispatcherIO: CoroutineDispatcher = Dispatchers.IO
var dispatcherBackground: CoroutineDispatcher = Dispatchers.Default

fun launchCoroutineUI(block: suspend CoroutineScope.() -> Unit) =
    CoroutineScope(dispatcherMain).launch { block() }

fun launchCoroutineBackground(block: suspend CoroutineScope.() -> Unit) =
    CoroutineScope(dispatcherBackground).launch { block() }

fun launchCoroutineIO(block: suspend CoroutineScope.() -> Unit) =
    CoroutineScope(dispatcherIO).launch { block() }

fun CoroutineScope.launchUI(
    exceptionHandler: CoroutineExceptionHandler = CoroutineExceptionHandler { _, _ -> run {} },
    block: suspend CoroutineScope.() -> Unit
): Job {
    return launch(dispatcherMain + exceptionHandler, CoroutineStart.DEFAULT, block)
}

fun CoroutineScope.launchBackground(
    exceptionHandler: CoroutineExceptionHandler = CoroutineExceptionHandler { _, _ -> run {} },
    block: suspend CoroutineScope.() -> Unit
): Job {
    return launch(dispatcherBackground + exceptionHandler, CoroutineStart.DEFAULT, block)
}

fun CoroutineScope.launchIO(
    exceptionHandler: CoroutineExceptionHandler = CoroutineExceptionHandler { _, _ -> run {} },
    block: suspend CoroutineScope.() -> Unit
): Job {
    return launch(dispatcherIO + exceptionHandler, CoroutineStart.DEFAULT, block)
}

suspend fun <T> withUiContext(block: suspend CoroutineScope.() -> T): T = withContext(dispatcherMain, block)
suspend fun <T> withBackgroundContext(block: suspend CoroutineScope.() -> T): T = withContext(dispatcherBackground, block)
suspend fun <T> withIoContext(block: suspend CoroutineScope.() -> T): T = withContext(dispatcherIO, block)
