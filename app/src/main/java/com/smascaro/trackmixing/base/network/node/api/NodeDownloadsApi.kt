package com.smascaro.trackmixing.base.network.node.api

import okhttp3.ResponseBody
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Streaming

interface NodeDownloadsApi {
    @Streaming
    @GET("/fetch/{videoId}/low")
    suspend fun downloadTrack(@Path("videoId") videoId: String): ResponseBody
}