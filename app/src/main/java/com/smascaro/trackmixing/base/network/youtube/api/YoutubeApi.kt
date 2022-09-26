package com.smascaro.trackmixing.base.network.youtube.api

import com.smascaro.trackmixing.base.network.youtube.model.SearchResultResponseSchema
import com.smascaro.trackmixing.base.network.youtube.model.VideoDetailsResponseSchema
import retrofit2.http.GET
import retrofit2.http.Query

interface YoutubeApi {
    // TODO add pagination
    @GET("search?type=video&part=snippet&maxResults=20")
    suspend fun search(
        @Query("q") query: String,
        @Query("key") apiKey: String
    ): SearchResultResponseSchema

    @GET("videos?part=contentDetails")
    suspend fun fetchDetails(
        @Query("id") videoIds: String,
        @Query("key") apiKey: String
    ): VideoDetailsResponseSchema
}