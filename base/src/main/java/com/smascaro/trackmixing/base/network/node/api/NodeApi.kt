package com.smascaro.trackmixing.base.network.node.api

import com.smascaro.trackmixing.base.network.node.model.FetchProgressResponseSchema
import com.smascaro.trackmixing.base.network.node.model.RequestTrackResponseSchema
import com.smascaro.trackmixing.base.network.youtube.model.FetchTrackDetailsResponseSchema
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path

interface NodeApi {
    @POST("/demucs/{videoUrl}")
    suspend fun requestTrack(@Path("videoUrl") videoUrl: String): RequestTrackResponseSchema

    @GET("/progress/{videoId}")
    suspend fun fetchProgress(@Path("videoId") videoId: String): FetchProgressResponseSchema

    @GET("/details/{videoId}")
    suspend fun fetchDetails(@Path("videoId") videoId: String): FetchTrackDetailsResponseSchema
}