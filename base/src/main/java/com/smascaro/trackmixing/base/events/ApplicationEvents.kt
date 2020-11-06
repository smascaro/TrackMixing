package com.smascaro.trackmixing.base.events

class ApplicationEvent(val state: AppState) {
    sealed class AppState {
        class Background : AppState()
        class Foreground : AppState()
    }
}