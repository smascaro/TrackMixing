package com.smascaro.trackmixing.search.business.download.model

class ApplicationEvent(val state: AppState) {
    sealed class AppState {
        class Background : AppState()
        class Foreground : AppState()
    }
}