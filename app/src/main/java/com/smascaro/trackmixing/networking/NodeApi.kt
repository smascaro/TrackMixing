package com.smascaro.trackmixing.networking

import com.smascaro.trackmixing.networking.availableTracks.AvailableTracksResponseSchema
import retrofit2.http.GET
import retrofit2.Call
import retrofit2.http.Query

interface NodeApi {
    @GET("/availableTracks")
    fun fetchAvailableTracks(
        @Query("sort") criteria: String,
        @Query("limit") limit: Int?,
        @Query("offset") offset: Int?
    ): Call<AvailableTracksResponseSchema>
}