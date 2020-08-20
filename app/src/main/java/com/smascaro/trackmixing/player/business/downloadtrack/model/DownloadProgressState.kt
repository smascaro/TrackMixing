package com.smascaro.trackmixing.player.business.downloadtrack.model

import com.smascaro.trackmixing.common.data.model.NotificationData

data class DownloadProgressState(
    val trackTitle: String,
    val progress: Int,
    val statusMessage: String
) : NotificationData