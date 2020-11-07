package com.smascaro.trackmixing.base.events

sealed class UiProgressEvent {
    class ProgressUpdate(val progress: Int, val status: String) : UiProgressEvent()
    object ProgressFinished : UiProgressEvent()
    class ErrorOccurred(val message: String)
}
