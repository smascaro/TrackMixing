package com.smascaro.trackmixing.player.business.downloadtrack.model

class ApplicationEvent(val state: AppState) {
    sealed class AppState {
        class Background : AppState()
        class Foreground : AppState()
    }
}