package com.smascaro.trackmixing.player.business.downloadtrack.business

import com.smascaro.trackmixing.common.data.datasource.network.NodeApi
import timber.log.Timber
import javax.inject.Inject

class RequestTrackUseCase @Inject constructor(private val nodeApi: NodeApi) {
    sealed class Result {
        class Success(val videoId: String) : Result()
        class Failure(val error: Throwable) : Result()
    }

    private var videoId: String? = null

    suspend fun execute(url: String): Result {
        return try {
            val requestVideoId = nodeApi.requestTrack(url)
            videoId = requestVideoId.body.track_id
            Result.Success(videoId!!)
        } catch (e: Exception) {
            Timber.e(e)
            Result.Failure(e)
        }
    }
}