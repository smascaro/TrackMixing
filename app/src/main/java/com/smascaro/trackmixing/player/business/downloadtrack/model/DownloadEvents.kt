package com.smascaro.trackmixing.player.business.downloadtrack.model

sealed class DownloadEvents {
    data class ProgressUpdate(
        val trackTitle: String,
        val progress: Int,
        val message: String
    ) :
        DownloadEvents()

    class FinishedProcessing : DownloadEvents()
    class ErrorOccurred(val message: String) : DownloadEvents()
}