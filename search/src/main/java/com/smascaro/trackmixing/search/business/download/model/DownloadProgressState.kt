package com.smascaro.trackmixing.search.business.download.model

import com.smascaro.trackmixing.base.service.NotificationData

data class DownloadProgressState(
    val trackTitle: String,
    val progress: Int,
    val statusMessage: String
) : NotificationData