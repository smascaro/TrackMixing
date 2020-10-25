package com.smascaro.trackmixing.common.data.datasource.network

import com.smascaro.trackmixing.common.data.network.AvailableTracksResponseSchema
import com.smascaro.trackmixing.common.data.network.FetchProgressResponseSchema
import com.smascaro.trackmixing.common.data.network.RequestTrackResponseSchema
import com.smascaro.trackmixing.player.business.downloadtrack.model.FetchTrackDetailsResponseSchema
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query

interface NodeApi {
    @GET("/availableTracks")
    fun fetchAvailableTracks(
        @Query("sort") criteria: String,
        @Query("limit") limit: Int?,
        @Query("offset") offset: Int?
    ): Call<AvailableTracksResponseSchema>

    @POST("/demucs/{videoUrl}")
    suspend fun requestTrack(@Path("videoUrl") videoUrl: String): RequestTrackResponseSchema

    @GET("/progress/{videoId}")
    suspend fun fetchProgress(@Path("videoId") videoId: String): FetchProgressResponseSchema

    @GET("/details/{videoId}")
    suspend fun fetchDetails(@Path("videoId") videoId: String): FetchTrackDetailsResponseSchema
}