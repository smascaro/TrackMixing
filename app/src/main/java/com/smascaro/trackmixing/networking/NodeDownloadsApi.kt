package com.smascaro.trackmixing.networking

import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Streaming

interface NodeDownloadsApi {
    @Streaming
    @GET("/fetch/{videoId}/low")
    fun downloadTrack(@Path("videoId") videoId: String): Call<ResponseBody>
}