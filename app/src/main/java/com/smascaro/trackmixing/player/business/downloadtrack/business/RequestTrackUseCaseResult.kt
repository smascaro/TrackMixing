package com.smascaro.trackmixing.player.business.downloadtrack.business

sealed class RequestTrackUseCaseResult {
    class Success : RequestTrackUseCaseResult()
    class Failure(val message: String) : RequestTrackUseCaseResult()
    class NetworkError(val message: String) : RequestTrackUseCaseResult()
}