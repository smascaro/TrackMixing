package com.smascaro.trackmixing.search.business.download.usecase

import com.smascaro.trackmixing.base.network.node.api.NodeApi
import timber.log.Timber
import java.net.SocketTimeoutException
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
        } catch (se: SocketTimeoutException) {
            Timber.e(se)
            Result.Failure(Exception("Server unreachable", se))
        } catch (e: Exception) {
            Timber.e(e)
            Result.Failure(e)
        }
    }
}