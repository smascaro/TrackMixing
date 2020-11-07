package com.smascaro.trackmixing.base.network.testdata.api

import okhttp3.ResponseBody
import retrofit2.http.GET
import retrofit2.http.Path

interface TestDataFilesApi {
    @GET("{file}")
    suspend fun downloadTestItemBundle(@Path("file") zipFileName: String): ResponseBody
}