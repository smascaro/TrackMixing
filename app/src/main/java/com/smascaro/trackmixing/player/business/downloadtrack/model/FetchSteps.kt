package com.smascaro.trackmixing.player.business.downloadtrack.model

sealed class FetchSteps(val percentage: Int) {
    class ServerProcessStep : FetchSteps(90)
    class DownloadStep : FetchSteps(10)
}