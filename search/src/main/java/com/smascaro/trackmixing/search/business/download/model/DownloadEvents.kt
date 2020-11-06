package com.smascaro.trackmixing.search.business.download.model

sealed class DownloadEvents {
    data class ProgressUpdate(
        val trackTitle: String,
        val progress: Int,
        val message: String,
        val step: FetchSteps
    ) :
        DownloadEvents() {
        override fun equals(other: Any?): Boolean {
            if (other == null || other !is ProgressUpdate) return false
            return this.trackTitle == other.trackTitle &&
                this.progress == other.progress &&
                this.message == other.message &&
                this.step::class.java == other.step::class.java
        }
    }

    object FinishedProcessing : DownloadEvents()
    object FinishedDownloading : DownloadEvents()
    class ErrorOccurred(val message: String) : DownloadEvents()
}

fun DownloadEvents.ProgressUpdate.toNotificationData(): DownloadProgressState {
    return DownloadProgressState(trackTitle, evaluateOverallProgress(), message)
}

fun DownloadEvents.ProgressUpdate.evaluateOverallProgress(): Int {
    return when (step) {
        is FetchSteps.ServerProcessStep -> (this.progress * (this.step.percentage.toFloat() / 100f)).toInt()
        is FetchSteps.DownloadStep -> FetchSteps.ServerProcessStep().percentage + (this.progress * (this.step.percentage.toFloat() / 100f)).toInt()
    }
}