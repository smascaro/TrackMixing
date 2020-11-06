package com.smascaro.trackmixing.search.business.download.usecase

sealed class RequestTrackUseCaseResult {
    object Success : RequestTrackUseCaseResult()
    class Failure(val message: String) : RequestTrackUseCaseResult()
    class NetworkError(val message: String) : RequestTrackUseCaseResult()
}