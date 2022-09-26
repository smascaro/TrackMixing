package com.smascaro.trackmixing.search.business.download.model

sealed class FetchSteps(val percentage: Int) {
    class ServerProcessStep : FetchSteps(90)
    class DownloadStep : FetchSteps(10)
}