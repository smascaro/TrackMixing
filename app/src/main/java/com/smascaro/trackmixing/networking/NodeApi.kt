package com.smascaro.trackmixing.networking

import com.smascaro.trackmixing.networking.availableTracks.AvailableTracksResponseSchema
import okhttp3.ResponseBody
import retrofit2.http.GET
import retrofit2.Call
import retrofit2.http.Path
import retrofit2.http.Query

interface NodeApi {
    @GET("/availableTracks")
    fun fetchAvailableTracks(
        @Query("sort") criteria: String,
        @Query("limit") limit: Int?,
        @Query("offset") offset: Int?
    ): Call<AvailableTracksResponseSchema>

    @GET("/fetch/{videoId}")
    fun fetchSong(@Path("videoId") videoId: String): Call<ResponseBody>
}