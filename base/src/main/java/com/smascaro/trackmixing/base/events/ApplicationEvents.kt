package com.smascaro.trackmixing.base.events

class ApplicationEvent(val state: AppState) {
    sealed class AppState {
        object Background : AppState()
        object Foreground : AppState()
    }
}