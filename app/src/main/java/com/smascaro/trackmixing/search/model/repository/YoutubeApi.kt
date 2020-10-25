package com.smascaro.trackmixing.search.model.repository

import com.smascaro.trackmixing.search.model.SearchResultResponseSchema
import com.smascaro.trackmixing.search.model.VideoDetailsResponseSchema
import retrofit2.http.GET
import retrofit2.http.Query

interface YoutubeApi {
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