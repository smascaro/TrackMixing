package com.smascaro.trackmixing.search.model.repository

import com.smascaro.trackmixing.search.model.SearchResultResponseSchema
import com.smascaro.trackmixing.search.model.VideoDetailsResponseSchema
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface YoutubeApi {
    @GET("search?type=video&part=snippet&maxResults=20")
    fun search(
        @Query("q") query: String,
        @Query("key") apiKey: String
    ): Call<SearchResultResponseSchema>

    @GET("videos?part=contentDetails")
    fun getDetails(
        @Query("id") videoIds: String,
        @Query("key") apiKey: String
    ): Call<VideoDetailsResponseSchema>
}