package com.smascaro.trackmixing.main.components.progress.model

sealed class UiProgressEvent {
    class ProgressUpdate(val progress: Int, val status: String) : UiProgressEvent()
    class ProgressFinished : UiProgressEvent()
    class ErrorOccurred(val message: String)
}
